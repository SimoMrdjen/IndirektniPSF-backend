package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.ObrazacIOService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.obrazac5.obrazacZb.ObrazacZbRepository;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.AbParameterService;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import IndirektniPSF.backend.parameters.StatusService;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniDetailsService;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ZakljucniListZbService extends AbParameterService implements IZakListService {

    private final ZakljucniListZbRepository zakljucniRepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
    private final ZakljucniDetailsService zakljucniDetailsService;
    private StringBuilder responseMessage =  new StringBuilder();
    private final ObrKontrService obrKontrService;
    private final ExcelService excelService;
    private final ZakljucniListMapper mapper;
    private final StatusService statusService;
    private final ObrazacIOService obrazacIoService;


    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveZakljucniFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());
        Integer year = excelService.readCellByIndexes(file.getInputStream(), 3,4);
        Integer jbbk =  excelService.readCellByIndexes(file.getInputStream(), 2,1);
        Integer excelKvartal =  excelService.readCellByIndexes(file.getInputStream(), 3,1);
        //chekIfKvartalIsCorrect(kvartal, excelKvartal, year);

        List<ZakljucniListDto> dtos =mapper.mapExcelToPojo(file.getInputStream());

        User user = this.getUser(email);
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
        this.checkIfKvartalIsForValidPeriod(kvartal, year);
    }

    public boolean checkIfExistValidZakList(Integer kvartal, Integer jbbks) {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);

        if (optionalZb.isEmpty() || optionalZb.get().getRadna() == 0 || optionalZb.get().getSTORNO() == 1) {
            return false;
        }
        return true;
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidZListAndFindVersion(Integer kvartal, Integer jbbks ) throws Exception {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);

        if (optionalZb.isEmpty()) {
            return 1;
        }
        ZakljucniListZb zb = optionalZb.get();

        if (zb.getRadna() == 1 && zb.getSTORNO() == 0 ) {
            throw new Exception("Za tekući kvartal već postoji učitan \nvažeći ZaključniList!\nUkoliko zelite da ucitate novu verziju " +
                    "\nprethodnu morate stornirati!");
        }
        return zb.getVerzija() + 1;
    }
// RAISING STATUS
    public ObrazacResponse findValidObrazacToRaise(String email, Integer status) throws Exception {

        var jbbks = this.getJbbksIBK(email);

        ZakljucniListZb zb =
               zakljucniRepository.findFirstByJbbkIndKorOrderByGenMysqlDesc( jbbks)
                       .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        statusService.resolveObrazacAccordingStatus(zb, status);
        return mapper.toResponse(zb);
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

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String raiseStatus(Integer id, String email) throws Exception {
        User user = this.getUser(email);

        var zb = zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));

        if (zb.getSTATUS() >= 20 || zb.getSTORNO() == 1) {
            throw new Exception("Zakljucni list ima status veci od 10 \n" +
                    "ili je vec storniran");
        }
        return statusService.raiseStatusDependentOfActuallStatus(zb, user, zakljucniRepository );
    }

    public ZakljucniListZb findZakListById(Integer id) {
        return   zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));
    }

 //STORNO

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoZakList(Integer id, String email) throws Exception {

        User user = this.getUser(email);
        var zb = this.findZakListById(id);
        this.checkStatusAndStorno(zb);
        zb.setSTORNO(1);
        zb.setRadna(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
        //TODO dodati opis storno
        zb.setOPISSTORNO("Naknadno");
        zakljucniRepository.save(zb);
        return "Zakljucni list je storniran!"
                + obrazacIoService.stornoIOAfterStornoZakList(user, zb.getKojiKvartal());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ObrazacResponse findValidObrazacToStorno(String email, Integer kvartal) throws Exception {

        var jbbks = this.getJbbksIBK(email);
        ZakljucniListZb zb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks)
                        .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        return mapper.toResponse(zb);
    }

    public void isObrazacStorniran(ZakljucniListZb zb) throws Exception {
        if( zb.getSTORNO() == 1) {
            throw  new Exception("Obrazac je storniran , \n`morate ucitati novu verziju!");
        }
    }
}
