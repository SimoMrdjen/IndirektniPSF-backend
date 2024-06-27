package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.idempotency.Idempotency;
import IndirektniPSF.backend.idempotency.IdempotencyRepository;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ObrazacType;
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
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ZakljucniListZbService extends AbParameterService implements IfObrazacChecker, IfObrazacService<ZakljucniListZb> {

    private final ZakljucniListZbRepository zakljucniRepository;
    private final SekretarijarService sekretarijarService;

    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
    private final ZakljucniDetailsService zakljucniDetailsService;
    private StringBuilder responseMessage = new StringBuilder();
    private final ObrKontrService obrKontrService;
    private final ExcelService excelService;
    private final ZakljucniListMapper mapper;
    private final StatusService statusService;
    private final ObrazacIOService obrazacIoService;
    private final IdempotencyRepository idempotencyRepository;



    public String processFile(UUID idempotencyKey, MultipartFile file, Integer kvartal, String email) throws Exception {
        if (idempotencyRepository.existsById(idempotencyKey)) {
            return "Request already processed.";
        }

        // Your file processing logic
        StringBuilder message = saveObrazacFromExcel(file, kvartal, email);

        // Save the idempotency key in the database
        idempotencyRepository.save(new Idempotency(idempotencyKey, "Processed"));

        return message.toString();
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());
        Integer year = excelService.readCellByIndexes(file.getInputStream(), 3, 4);
        Integer jbbk = excelService.readCellByIndexes(file.getInputStream(), 2, 1);
        Integer excelKvartal = excelService.readCellByIndexes(file.getInputStream(), 3, 1);
        chekIfKvartalIsCorrect(kvartal, excelKvartal, year);

        List<ZakljucniListDto> dtos = mapper.mapExcelToPojo(file.getInputStream());

        User user = this.getUser(email);
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        //provere
        checkIfKonto999999Exist(dtos);
        checkDuplicatesKonta(dtos);
        Integer version = checkIfExistValidZListAndFindVersion(jbbk, kvartal);
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

        if (isDoubledRecord(zb)) {
            return responseMessage;
        }
        var zbSaved = zakljucniRepository.save(zb);
        zakljucniDetailsService.saveDetailsExcel(dtos, zbSaved);
        return responseMessage;
    }

    public void checkIfKonto999999Exist(List<ZakljucniListDto> dtos) throws ObrazacException {
        for (ZakljucniListDto dto : dtos) {
            if ("999999".equals(dto.getKonto())) {
                throw new ObrazacException("U obrascu imate konto 999999!");
            }
        }
    }

    private boolean isDoubledRecord(ZakljucniListZb zb) {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository
                        .findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(
                                zb.getKojiKvartal(),
                                zb.getJbbkIndKor()
                        );
        if (optionalZb.isPresent() && optionalZb.get().getVerzija() == zb.getVerzija()) {
               return true;
        }
        return false;
    }

    //   @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidZListAndFindVersion(Integer jbbks, Integer kvartal) throws Exception {

        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks);
        if (optionalZb.isEmpty()) {
            return 1;
        }
        ZakljucniListZb zb = optionalZb.get();
        //TODO checkIfExistValidObrazacYet(zb);
        return zb.getVerzija() + 1;
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email, Integer status, Integer kvartal) throws Exception {

        var jbbks = this.getJbbksIBK(email);
        ZakljucniListZb zb = findLastObrazacForKvartal(jbbks, kvartal);
        this.isObrazacStorniran(zb);
        //check next
        ObrazacIO obrazacIO =
                obrazacIoService.findLastOptionalIOForKvartal(email, kvartal)
                        .orElseThrow(() -> new ObrazacException("Nije moguce overavanje obrrasca\n" +
                                "jer ne postoji ucitan Obrazac IO.\n" +
                                "Morate prethodno ucitati Obrazac IO!"));

        if (obrazacIO.getSTORNO() == 1) {
            throw new ObrazacException("Nije moguce overavanje obrasca jer je Obrazac IO storniran.\n" +
                    " Morate prethodno ucitati Obrazac IO!!");
        }
        statusService.resolveObrazacAccordingNextObrazac(zb, obrazacIO);
        statusService.resolveObrazacAccordingStatus(zb, status);
        return List.of(mapper.toResponse(zb));
    }

    public ZakljucniListZb findLastObrazacForKvartal(Integer jbbks, Integer kvartal) throws ObrazacException {
        return
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks)
                        .orElseThrow(() -> new ObrazacException("Ne postoji ucitan dokument!"));
    }

    public Optional<ZakljucniListZb> findLastObrazacForKvartalOptional(Integer jbbks, Integer kvartal) {
        return
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks);
    }

    public void checkDuplicatesKonta(List<ZakljucniListDto> dtos) throws Exception {

        var validError = obrKontrService.isKontrolaMandatory(9);
        var isKontrolaActive = obrKontrService.isKontrolaActive(9);

        List<String> kontos =
                dtos.stream()
                        .map(dto -> dto.getKonto().trim())
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
            } else if (!duplicates.isEmpty() && !validError) {
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

    @Override
    public ZakljucniListZb findObrazacById(Integer id, Integer kvartal) {

        return zakljucniRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Zakljucni list ne postoji!"));
    }

    //STORNO

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoZakList(Integer id, String email, Integer kvartal, String opis) throws Exception {

        User user = this.getUser(email);
        var zb = this.findObrazacById(id, kvartal);
        this.checkStatusAndStorno(zb);
        zb.setSTORNO(1);
        zb.setRADNA(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
        //TODO dodati opis storno
        zb.setOPISSTORNO(opis);
        zakljucniRepository.save(zb);
        return "Zakljucni list je storniran!\n";
             //TODO   + obrazacIoService.stornoIOAfterStornoZakList(user, zb.getKojiKvartal());
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


    public ObrazacResponse getZakListResponse(Integer jbbks, Integer kvartal) {

        Optional<ZakljucniListZb> optionalZakljucniListZb =
                findLastObrazacForKvartalOptional(jbbks, kvartal);
        if (optionalZakljucniListZb.isPresent()) {
            ObrazacResponse zakListResponse = mapper.toResponse(optionalZakljucniListZb.get());
            zakListResponse.setObrazacType(ObrazacType.ZAKLJUCNI_LIST);
            return zakListResponse;
        }
        var response = new ObrazacResponse();
        response.setObrazacType(ObrazacType.ZAKLJUCNI_LIST);
        return response;
    }

    public ObrazacResponse getObrazactWithDetailsForResponseById(Integer id, Integer kvartal) {

        ZakljucniListZb zb = findObrazacById(id, kvartal);

        List<ZakljucniListDto> detailDtos =
                zb.getStavke().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        ObrazacResponse response = mapper.toResponse(zb);
        response.setZakljucniListDtos(detailDtos);
        response.setObrazacType(ObrazacType.ZAKLJUCNI_LIST);
        return response;
    }
}
