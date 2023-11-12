package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;


import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom.ObrazacIOMapper;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.obrazac5.obrazacZb.ObrazacZbService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.AbParameterService;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbRepository;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Component
public class ObrazacIOService extends AbParameterService {

    private final ObrazacIORepository obrazacIOrepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final ObrazacIODetailService obrazacIODetailService;
    private final UserRepository userRepository;
    private final ObrazacZbService obrazacService;
    private StringBuilder responseMessage =  new StringBuilder();
    private final ExcelService excelService;
//    private final ZakljucniListZbService zakljucniListZbService;

    private final ObrazacIOMapper mapper;
    private final ZakljucniListZbRepository zakljucniRepository;



    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveIOFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        User user = this.getUser(email);
        Integer jbbks = this.getJbbksIBK(user);
        Integer version = checkIfExistValidZListAndFindVersion( jbbks, kvartal);
        responseMessage.delete(0, responseMessage.length());
        ZakljucniListZb zakList = this.findValidZakList(kvartal, jbbks);
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;


        try {
            Integer year = excelService.readCellByIndexes(file.getInputStream(), 2,3);
            Integer jbbkExcel = excelService.readCellByIndexes(file.getInputStream(), 2,1);
            //chekIfKvartalIsCorrect(kvartal, excelKvartal, year);

            List<ObrazacIODTO> dtos =mapper.mapExcelToPojo(file.getInputStream());

            //provere
//        checkDuplicatesKonta(dtos);
            checkJbbks(user, jbbkExcel);

            Obrazac5_pom_zb obrIO = Obrazac5_pom_zb.builder()
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

            Obrazac5_pom_zb obrIOSaved = obrazacIOrepository.save(obrIO);

           var details = obrazacIODetailService.saveListOfObrazac5_pom(dtos, obrIOSaved, zakList.getStavke());
            //obrazacIODetailService.compareIoDetailsWithZakListDetails(details, zakList.getStavke());
            return responseMessage;

        } catch (Exception ex) {
            System.out.println( "Exception occurred while processing the file" + ex);
            throw ex;
        }
    }

//    public void checkIfExistValidZakList(Integer kvartal, Integer jbbks) throws Exception {
//
//        if( !this.findValidZakList(kvartal,jbbks)) {
//            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
//                    "Zakljucni list. Prvo ucitajte \n Zakljucni list!");
//        }
//    }

    private ZakljucniListZb findValidZakList(Integer kvartal, Integer jbbks) throws Exception {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);

        if (optionalZb.isEmpty() || optionalZb.get().getRadna() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Zakljucni list. Prvo ucitajte \n Zakljucni list!");
        }
        return optionalZb.get();
    }

    private Integer checkIfExistValidZListAndFindVersion(Integer jbbk, Integer kvartal) throws Exception {

        Optional<Obrazac5_pom_zb> optionalZb =
               obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk , kvartal);
        if (optionalZb.isEmpty()) {
            return 1;
        }
        Obrazac5_pom_zb zb = optionalZb.get();

        if (zb.getRADNA() == 1 && zb.getSTORNO() == 0 ) {
            throw new Exception("Za tekući kvartal već postoji učitan \nvažeći Obrazac IO!\nUkoliko zelite da ucitate novu verziju " +
                    "\nprethodnu morate stornirati!");
        }
        return zb.getVERZIJA() + 1;
    }
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIOAfterStornoZakList(User user, Integer kvartal) throws Exception {

        var optionalIO = findLastVersionOfObrIO(user,kvartal);

        if (optionalIO.isEmpty() || optionalIO.get().getRADNA() == 0 || optionalIO.get().getSTORNO() == 1) {
            return "";
        }
        var io = optionalIO.get();

        io.setSTORNO(1);
        io.setRADNA(0);
        io.setSTOSIFRAD(user.getSifraradnika());
        io.setOPISSTORNO("Storniran prethodni dokument!");
            
        obrazacIOrepository.save(io);
        return "Obrazac IO je storniran!" + obrazacService.stornoObrAfterObrIO(user, kvartal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIO(Integer id, String email) {
        var user = this.getUser(email);
        var io = obrazacIOrepository.findById(id).get();
            io.setSTORNO(1);
            io.setRADNA(0);
            io.setSTOSIFRAD(user.getSifraradnika());
        obrazacIOrepository.save(io);
        return "Obrazac IO je storniran!";
    }

    public Optional<Obrazac5_pom_zb> findLastVersionOfObrIO(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
    }

    public ObrazacResponse findValidObrazacToStorno(String email, Integer kvartal) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrIOFromUser(Integer id, String email, Integer kvartal) {
        return null;
    }

    public ObrazacResponse findValidObrazacToRaise(String email, Integer status) {
        return null;
    }



    //  @Transactional
//    public Obrazac5_pom_zb saveObrazacIO(List<ObrazacIODTO> dtos, Integer kvartal, Integer year, String email) {
//
//        var user = userRepository.findByEmail(email).orElseThrow();
//
//        Integer sifSekret = user.getZa_sif_sekret(); //fetch from table user-bice- user.getZa_sif_sekret();
//        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret); //fetch from table user or sekr, im not sure
//        Integer jbbk = pPartnerService.getJBBKS(user.getSifra_pp());
//        Integer version = checkIfExistValidZListAndFindVersion(jbbk, kvartal);
//        Integer todayInt = (int) LocalDate.now().toEpochDay() + 25569;
//
//        Obrazac5_pom_zb obrIO = Obrazac5_pom_zb.builder()
//                .KOJI_KVARTAL(kvartal)
//                .GODINA(year)
//                .VERZIJA(version)
//                .RADNA(1)
//                .SIF_SEKRET(sifSekret)
//                .RAZDEO(sekretarijat.getRazdeo())
//                .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
//                .JBBK_IND_KOR(jbbk)
//                .SIF_RAC(1)
//                .DINARSKI(1)
//                .STATUS(0)
//                .POSLATO_O(0)
//                .POVUCENO(0)
//                .KONACNO(0)
//                .POSLAO_NAM(user.getSifraradnika())
//                .DATUM_DOK(todayInt)
//                .PODIGAO_STATUS(0)
//                .DATUM_POD_STATUSA(0)
//                .POSLAO_U_ORG(0)
//                .DATUM_SLANJA(0)
//                .POSLAO_IZ_ORG(0)
//                .DATUM_ORG(0)
//                .ZAPRIMIO_VER(0)
//                .OVERIO_VER(0)
//                .ODOBRIO_VER(0)
//                .PROKNJIZENO(0)
//                .XLS(1)
//                .STORNO(0)
//                .STOSIFRAD(0)
//                .GEN_OPENTAB(0)
//                .build();
//        Obrazac5_pom_zb obrIOSaved = obrazacIOrepository.save(obrIO);
//
//        obrazacIODetailService.saveListOfObrazac5_pom(dtos, obrIOSaved);
//        return obrIOSaved;
//    }
}
