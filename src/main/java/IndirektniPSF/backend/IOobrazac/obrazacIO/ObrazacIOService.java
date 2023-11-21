package IndirektniPSF.backend.IOobrazac.obrazacIO;


import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIOMapper;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5Service;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Component
public class ObrazacIOService extends AbParameterService implements IfObrazacChecker, IfObrazacService<ObrazacIO> {

    private final ObrazacIORepository obrazacIOrepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final ObrazacIODetailService obrazacIODetailService;
    private final UserRepository userRepository;
    private final Obrazac5Service obrazac5Service;
    private StringBuilder responseMessage =  new StringBuilder();
    private final ExcelService excelService;
//    private final ZakljucniListZbService zakljucniListZbService;
    private final ObrazacIOMapper mapper;
    private final ZakljucniListZbRepository zakljucniRepository;

    private final StatusService statusService;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        User user = this.getUser(email);
        Integer jbbks = this.getJbbksIBK(user);
        Integer version = checkIfExistValidObrazacIOAndFindVersion( jbbks, kvartal);
        responseMessage.delete(0, responseMessage.length());
        ZakljucniListZb zakList = this.findValidZakList(kvartal, jbbks);
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;


        try {
            Integer year = excelService.readCellByIndexes(file.getInputStream(), 2,3);
            Integer jbbkExcel = excelService.readCellByIndexes(file.getInputStream(), 2,1);
//          chekIfKvartalIsCorrect(kvartal, kvartal, year); //TODO uncomment in production
            List<ObrazacIODTO> dtos =mapper.mapExcelToPojo(file.getInputStream());
//          checkDuplicatesKonta(dtos);
            checkJbbks(user, jbbkExcel);

            ObrazacIO obrIO = ObrazacIO.builder()
                    .KOJI_KVARTAL(kvartal)
                    .GODINA(year)
                    .VERZIJA(version)
                    .RADNA(1)
                    .SIF_SEKRET(sifSekret)
                    .RAZDEO(sekretarijat.getRazdeo())
                    .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
                    .JBBK_IND_KOR(jbbkExcel)
                    .SIF_RAC(1)
                    .DINARSKI(1)
                    .STATUS(0)
                    .POSLATO_O(0)
                    .POVUCENO(0)
                    .KONACNO(0)
                    .POSLAO_NAM(user.getSifraradnika())
                    .DATUM_DOK(today)
                    .PODIGAO_STATUS(0)
                    .DATUM_POD_STATUSA(0)
                    .POSLAO_U_ORG(0)
                    .DATUM_SLANJA(0)
                    .POSLAO_IZ_ORG(0)
                    .DATUM_ORG(0)
                    .ZAPRIMIO_VER(0)
                    .OVERIO_VER(0)
                    .ODOBRIO_VER(0)
                    .PROKNJIZENO(0)
                    .XLS(1)
                    .STORNO(0)
                    .STOSIFRAD(0)
                    .GEN_OPENTAB(0)
                    .build();

            ObrazacIO obrIOSaved = obrazacIOrepository.save(obrIO);

           var details = obrazacIODetailService.saveListOfObrazac5_pom(dtos, obrIOSaved, zakList.getStavke());
//           obrazacIODetailService.compareIoDetailsWithZakListDetails(details, zakList.getStavke());//TODO implement this check
            return responseMessage;
        } catch (Exception ex) {
            System.out.println( "Exception occurred while processing the file" + ex);
            throw ex;
        }
    }

    private ZakljucniListZb findValidZakList(Integer kvartal, Integer jbbks) throws Exception {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);

        if (optionalZb.isEmpty() || optionalZb.get().getRADNA() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Zakljucni list. Prvo ucitajte \n Zakljucni list!");
        }
        return optionalZb.get();
    }

    private Integer checkIfExistValidObrazacIOAndFindVersion(Integer jbbk, Integer kvartal) throws Exception {

        Optional<ObrazacIO> optionalZb =
               obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk , kvartal);
        if (optionalZb.isEmpty()) {
            return 1;
        }
        ObrazacIO zb = optionalZb.get();
        checkIfExistValidObrazacYet(zb);
        return zb.getVERZIJA() + 1;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIOAfterStornoZakList(User user, Integer kvartal) throws Exception {

        var optionalIO = findLastOptionalIOForKvartal(user,kvartal);

        if (optionalIO.isEmpty() || optionalIO.get().getRADNA() == 0 || optionalIO.get().getSTORNO() == 1) {
            return "";
        }
        var io = optionalIO.get();

        io.setSTORNO(1);
        io.setRADNA(0);
        io.setSTOSIFRAD(user.getSifraradnika());
        io.setOPISSTORNO("Storniran prethodni dokument!");
        obrazacIOrepository.save(io);
        return "Obrazac IO je storniran!" + obrazac5Service.stornoObrAfterObrIO(user, kvartal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIO(Integer id, String email) {
        var user = this.getUser(email);
        var io = obrazacIOrepository.findById(id).get();
            io.setSTORNO(1);
            io.setRADNA(0);
            io.setSTOSIFRAD(user.getSifraradnika());
        obrazacIOrepository.save(io);
        return "Obrazac IO je storniran!" + obrazac5Service.stornoObrAfterObrIO(user, io.getKOJI_KVARTAL());
    }

    public Optional<ObrazacIO> findLastOptionalIOForKvartal(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
    }
    public Optional<ObrazacIO> findLastOptionalIOForKvartal(String email, Integer kvartal) {
        var jbbk = this.getJbbksIBK(email);
        return obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
    }
    public List<ObrazacResponse> findValidObrazacToStorno(String email, Integer kvartal) throws Exception {
        ObrazacIO zb = findLastOptionalIOForKvartal(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        this.isObrazacSentToDBK(zb);
        return List.of(mapper.toResponse(zb));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrIOFromUser(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
       var zb =  findObrazacById(id);
        this.checkStatusAndStorno(zb);
        zb.setSTORNO(1);
        zb.setRADNA(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
        zb.setOPISSTORNO("Naknadno");        //TODO dodati opis storno
        return "Obrazac IO je uspesno storniran!\n" + obrazac5Service.stornoObrAfterObrIO(user, kvartal);
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email,Integer status, Integer kvartal) throws Exception {

        Integer jbbks = getJbbksIBK(email);
        ObrazacIO zb = findLastOptionalIOForKvartal(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);

        statusService.resolveObrazacAccordingStatus(zb, status);
        //check next
        Obrazac5 obrazac5 =
                obrazac5Service.findLastVersionOfObrazac5Zb(jbbks, kvartal)
                        .orElseThrow(() -> new ObrazacException("Nije moguce odobravanje obrrasca\n" +
                                "jer ne postoji ucitan Obrazac 5.\n" +
                                "Morate prethodno ucitati Obrazac 5!"));

        if (obrazac5.getSTORNO() == 1) {
            throw new ObrazacException("Nije moguce odobravanje obrasca jer je Obrazac 5 storniran.\n" +
                    " Morate prethodno ucitati Obrazac 5!!");
        }
        statusService.resolveObrazacAccordingNextObrazac(zb, obrazac5);
        // check previous
        var zakList = this.findValidZakList(kvartal, jbbks);
        statusService.resolveObrazacAccordingPreviousObrazac(zb, zakList);
        return List.of(mapper.toResponse(zb));
    }

    public String raiseStatus(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
        var zb = findObrazacById(id);
        this.checkStatusAndStorno(zb);
        return String.valueOf(statusService.raiseStatusDependentOfActuallStatus(zb, user, obrazacIOrepository));
    }

    public ObrazacIO findObrazacById(Integer id) throws Exception {
        return
                 obrazacIOrepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ne postoji obrazac!"));
    }

}
