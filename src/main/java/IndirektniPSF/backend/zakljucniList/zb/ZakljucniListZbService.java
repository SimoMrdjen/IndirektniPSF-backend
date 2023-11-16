package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
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
public class ZakljucniListZbService extends AbParameterService implements IfObrazacChecker, IfObrazacService {

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


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidZListAndFindVersion(Integer jbbks, Integer kvartal) throws Exception {

        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);
        if (optionalZb.isEmpty()) {
            return 1;
        }
        ZakljucniListZb zb = optionalZb.get();
        checkIfExistValidObrazacYet(zb);
        return zb.getVerzija() + 1;
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email, Integer status, Integer kvartal) throws Exception {

        var jbbks = this.getJbbksIBK(email);
        ZakljucniListZb zb = findLastObrazacForKvartal(jbbks, kvartal);
        this.isObrazacStorniran(zb);
        statusService.resolveObrazacAccordingStatus(zb, status);
        return List.of(mapper.toResponse(zb));
    }
    public ZakljucniListZb findLastObrazacForKvartal(Integer jbbks, Integer kvartal) throws ObrazacException {
      return
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks)
                        .orElseThrow(() -> new ObrazacException("Ne postoji ucitan dokument!"));
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
                    throw new ObrazacException("Imate duplirana konta: " + duplicates);
                }
                else if (!duplicates.isEmpty() && !validError) {
                    responseMessage.append("Imate duplirana konta: " + duplicates);
                }
            }
        }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String raiseStatus(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
        var zb = zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));
        checkStatusAndStorno(zb);
        return String.valueOf(statusService.raiseStatusDependentOfActuallStatus(zb, user, zakljucniRepository));
    }

    public ZakljucniListZb findZakListById(Integer id) {
        return   zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));
    }

 //STORNO

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoZakList(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
        var zb = this.findZakListById(id);
        this.checkStatusAndStorno(zb);
        zb.setSTORNO(1);
        zb.setRadna(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
        //TODO dodati opis storno
        zb.setOPISSTORNO("Naknadno");
        zakljucniRepository.save(zb);
        return "Zakljucni list je storniran!\n"
                + obrazacIoService.stornoIOAfterStornoZakList(user, zb.getKojiKvartal());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ObrazacResponse> findValidObrazacToStorno(String email, Integer kvartal) throws Exception {

        var jbbks = this.getJbbksIBK(email);
        ZakljucniListZb zb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks)
                        .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        this.isObrazacSentToDBK(zb);
        return List.of(mapper.toResponse(zb));
    }
}
