package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.ParametersService;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniDetailsService;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ZakljucniListZbService implements IZakListService {

    private final ZakljucniListZbRepository zakljucniRepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
   // private final ObrazacZbRepository obrazacZbRepository;
    private final ZakljucniDetailsService zakljucniDetailsService;
    private StringBuilder responseMessage =  new StringBuilder();
    private final ObrKontrService obrKontrService;
    private final ExcelService excelService;
    private final ZakljucniListMapper mapper;
    private final ParametersService parameterService;


    @Transactional
    public StringBuilder saveZakljucniFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());
        Integer year = excelService.readCellByIndexes(file.getInputStream(), 3,4);
        Integer jbbk =  excelService.readCellByIndexes(file.getInputStream(), 2,1);
        Integer excelKvartal =  excelService.readCellByIndexes(file.getInputStream(), 3,1);
        //chekIfKvartalIsCorrect(kvartal, excelKvartal, year);

        List<ZakljucniListDto> dtos =mapper.mapExcelToPojo(file.getInputStream());


        User user = userRepository.findByEmail(email).orElseThrow();
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        //provere
        checkDuplicatesKonta(dtos);
        Integer version = checkIfExistValidZListAndFindVersion(kvartal, jbbk);
        checkJbbks(user, jbbk);

        var zb =
                ZakljucniListZb.builder()
                        .GEN_OPENTAB(0)
                        .GEN_APVDBK(0)
                        .kojiKvartal(kvartal)
                        .GODINA(year)
                        .verzija(version)
                        .radna(1)
                        .SIF_SEKRET(sifSekret)
                        .RAZDEO(sekretarijat.getRazdeo())
                        .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
                        .jbbkIndKor(jbbk)
                        .SIF_RAC(1)
                        .DINARSKI(1)
                        .STATUS(0)
                        .POSLATO_O(0)
                        .POVUCENO(0)
                        .KONACNO(0)
                        .POSLAO_NAM(0)
                        .DATUM_DOK(today)
                        .PROKNJIZENO(0)
                        .XLS(0)
                        .STORNO(0)
                        .STOSIFRAD(0)
                        .build();
        var zbSaved = zakljucniRepository.save(zb);

        zakljucniDetailsService.saveDetailsExcel(dtos, zbSaved);
        return responseMessage;
    }

    private void chekIfKvartalIsCorrect(Integer kvartal, Integer excelKvartal, Integer year) {
        if(kvartal != excelKvartal) {
            throw new IllegalArgumentException("Odabrani kvartal i kvartal u dokumentu nisu identicni!");
        }
        parameterService.checkIfKvartalIsForValidPeriod(kvartal, year);
    }


//    public Integer getJbbksIBK(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow();
//        return pPartnerService.getJBBKS(user.getSifra_pp());
//    }

    public void checkJbbks(User user, Integer jbbksExcell) throws Exception {
        var jbbkDb =parameterService.getJbbksIBK(user.getEmail()); //find  in PPARTNER by sifraPP in ind_lozinka

        if (!jbbkDb.equals(jbbksExcell)) {
            throw new Exception("Niste uneli (odabrali) vaš JBKJS!");
        }
    }
    @Transactional

    public Integer checkIfExistValidZListAndFindVersion(Integer kvartal, Integer jbbks ) throws Exception {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);

        if (optionalZb.isEmpty()) {
            return 1;
        }
        ZakljucniListZb zb = optionalZb.get();

        if (zb.getRadna() == 1 && zb.getSTORNO() == 0 && zb.getSTATUS() >= 10) {
            throw new Exception("Za tekući kvartal već postoji učitan \nvažeći ZaključniList koji je vec overen!");
        }
        if (zb.getRadna() == 1) {
            zb.setRadna(0);
            zakljucniRepository.save(zb);
        }
        return zb.getVerzija() + 1;
    }

    public ZaKListResponse findValidObrazacToRaise(String email, Integer status) throws Exception {

        var jbbks = parameterService.getJbbksIBK(email);
        Optional<ZakljucniListZb> optionalZb =
               zakljucniRepository.findFirstByJbbkIndKorOrderByGenMysqlDesc( jbbks);
        var zb = this.ifObrazacExistGetIt(optionalZb);
        this.isObrazacStorniran(zb);
        this.resolveObrazacAccordingStatus(zb, status);
        return mapper.toResponse(zb);
    }

    private void resolveObrazacAccordingStatus(ZakljucniListZb zb, Integer status) throws Exception {

        var actualStatus = zb.getSTATUS();
        if (actualStatus >= 20) {
            throw new Exception("Dokument je vec poslat Vasem DBK-u!");
        } else if(actualStatus == 0 && status == 10) {
            throw new Exception("Dokument jos nije odobren, \nidite na opciju odobravanje!");
        } else if(actualStatus == 10 && status == 0) {
            throw new Exception("Dokument je vec odobren, \nmozete ici na opciju overavanje!");
        }
    }

    public void isObrazacStorniran(ZakljucniListZb zb) throws Exception {
        if( zb.getSTORNO() == 1) {
            throw  new Exception("Obrazac je storniran , \n`morate ucitati novu verziju!");
        }
    }

    public ZakljucniListZb ifObrazacExistGetIt( Optional<ZakljucniListZb> optionalZb) {
        if (!optionalZb.isPresent()) {
            throw new IllegalArgumentException("Ne postoji ucitan dokument!");
        }
        ZakljucniListZb zb = optionalZb.get();
        return zb;
    }

    public void checkDuplicatesKonta(List<ZakljucniListDto> dtos) throws Exception {

            var validError = obrKontrService.isKontrolaMandatory(9);
            var isKontrolaActive = obrKontrService.isKontrolaActive(9);

        List<String> kontos =
                dtos.stream()
                        .map(dto -> dto.getProp1().trim())
                        .collect(Collectors.toList());

        List<String> duplicates = kontos.stream()
                .collect(Collectors.toMap(
                        e -> e,
                        v -> 1,
                        (existing, replacement) -> existing + replacement))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (isKontrolaActive) {
                if (!duplicates.isEmpty() && validError) {
                    throw new Exception("Imate duplirana konta: " + duplicates);
                }
                else if (!duplicates.isEmpty() && !validError) {
                    responseMessage.append("Imate duplirana konta: " + duplicates);
                }
            }
        }

    @Transactional
    public String raiseStatus(Integer id, String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow();

        var zb = zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));

        if (zb.getSTATUS() >= 20 || zb.getSTORNO() == 1) {
            throw new Exception("Zakljucni list ima status veci od 10 \n" +
                    "ili je vec storniran");
        }
        return raiseStatusDependentOfActuallStatus(zb, user);
    }

    @Transactional
    private String raiseStatusDependentOfActuallStatus(ZakljucniListZb zb, User user) {

        var status = zb.getSTATUS();
        if (status == 0) {
            zb.setPODIGAO_STATUS(user.getSifraradnika());
        } else {
            zb.setPOSLAO_NAM(user.getSifraradnika());
        }
        zb.setSTATUS(status + 10);
        var savedZb = zakljucniRepository.save(zb);

        return "Zakljucnom listu je status \npodignut na nivo " +
                savedZb.getSTATUS() + "!";
    }

    @Transactional
    public String stornoZakList(Integer id, String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow();
        var zb = zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));

        if (zb.getSTATUS() >= 20 || zb.getSTORNO() == 1) {
            throw new Exception("Zakljucni list ima status veci od 10 \n" +
                    "ili je vec storniran");
        }
        zb.setSTORNO(1);
        zb.setRadna(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
        zakljucniRepository.save(zb);
        return "Zakljucni list je storniran!";
    }



//    @Transactional
//    public StringBuilder saveZakljucniList(List<ZakljucniListDto> dtos,
//                                           Integer kvartal,
//                                           Integer jbbks,
//                                           Integer year,
//                                           String email) throws Exception {
//
//        responseMessage.delete(0, responseMessage.length());
//        User user = userRepository.findByEmail(email).orElseThrow();
//        Integer sifSekret = user.getZa_sif_sekret();
//        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
//        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
//        //provere
//        Integer version = checkIfExistValidZListAndFindVersion(kvartal, jbbks);
//        checkJbbks(user, jbbks);
//        checkDuplicatesKonta(dtos);
//
//        var zb =
//                ZakljucniListZb.builder()
//                        .GEN_OPENTAB(0)
//                        .GEN_APVDBK(0)
//                        .kojiKvartal(kvartal)
//                        .GODINA(year)
//                        .verzija(version)
//                        .radna(1)
//                        .SIF_SEKRET(sifSekret)
//                        .RAZDEO(sekretarijat.getRazdeo())
//                        .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
//                        .jbbkIndKor(jbbks)
//                        .SIF_RAC(1)
//                        .DINARSKI(1)
//                        .STATUS(0)
//                        .POSLATO_O(0)
//                        .POVUCENO(0)
//                        .KONACNO(0)
//                        .POSLAO_NAM(user.getSifraradnika())
//                        .DATUM_DOK(today)
//                        .PROKNJIZENO(0)
//                        .XLS(0)
//                        .STORNO(0)
//                        .STOSIFRAD(0)
//                        .build();
//        var zbSaved = zakljucniRepository.save(zb);
//
//        zakljucniDetailsService.saveDetails(dtos, zbSaved);
//        return responseMessage;
//    }


}
