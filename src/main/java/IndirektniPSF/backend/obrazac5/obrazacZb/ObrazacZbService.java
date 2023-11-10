package IndirektniPSF.backend.obrazac5.obrazacZb;

import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.Obrazac5_pom_zb;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.ObrazacIORepository;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac.ObrazacMapper;
import IndirektniPSF.backend.obrazac5.obrazac.ObrazacService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.AbParameterService;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
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
public class ObrazacZbService extends AbParameterService {
    private final ObrazacZbRepository obrazacZbRepository;
    private final SekretarijarService sekretarijarService;
    private final ObrazacService obrazacService;
    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
    private StringBuilder responseMessage =  new StringBuilder();
    private final ObrazacMapper mapper;
    private final ObrazacIORepository obrazacIOrepository;

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveZakljucniFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());

        User user = this.getUser(email);
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        var jbbk = getJbbksIBK(user);
        Obrazac5_pom_zb validIO = this. findValidIO(kvartal, jbbk);
//        checkDuplicatesKonta(dtos);
        Integer version = checkIfExistValidZListAndFindVersion( jbbk, kvartal);

        List<Obrazac5DTO> dtos =mapper.mapExcelToPojo(file.getInputStream());

        ObrazacZb zb = ObrazacZb.builder()
                //.gen_interbase(1)
                .koji_kvartal(kvartal)
                .tip_obrazca(5)
                .sif_sekret(sifSekret)
                .razdeo(sekretarijat.getRazdeo())
                .sif_rac(1)
                .verzija(version)
                .dinarski(1)
                .status(0)
                .poslato_o(0)
                .radna(1)
                .povuceno(0)
                .konacno(0)
                .poslao_nam(user.getSifraradnika())
                .poslao_u_org(0)
                .poslao_iz_org(0)
                .zaprimio_ver(0)
                .overio_ver(0)
                .odobrio_ver(0)
                .proknjizeno(0)
                .jbbk_ind_kor(jbbk)
                .storno(0)
                .stosifrad(0)
                .opisstorno("")
                .podigao_status(0)
                .datum_pod_statusa(0)
                .datum_org(0)
                .nivo_konsolidacije(0)
                .build();

        ObrazacZb zbSaved = obrazacZbRepository.save(zb);
        var details =  obrazacService.saveDetailsExcel(dtos, zbSaved, validIO.getStavke());

//        obrazacService.saveDetailsExcel(dtos, zbSaved);
        return responseMessage;
    }

    private Obrazac5_pom_zb findValidIO(Integer kvartal, Integer jbbk) throws Exception {
        Optional<Obrazac5_pom_zb> optionalZb =
                obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);

        if (optionalZb.isEmpty() || optionalZb.get().getRADNA() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Obrazac IO. Prvo ucitajte \n Obrazac IO!");
        }
        return optionalZb.get();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidZListAndFindVersion(Integer jbbks, Integer kvartal) {
        var optionalObrazacZb = this.findLastVersionOfObrazacZb(jbbks, kvartal);
        if (optionalObrazacZb.isEmpty()) {
        return 1;}
        return optionalObrazacZb.get().getVerzija() + 1;
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrAfterObrIO(User user, Integer kvartal) {

        Optional<ObrazacZb> optionalObrazacZb = this.findLastVersionOfObrazacZb(user, kvartal);

        if (optionalObrazacZb.isEmpty())
            return "";

        var obrazacZb = optionalObrazacZb.get();
        if (obrazacZb.getRadna() == 0 || obrazacZb.getStorno() == 1)
            return "";

        obrazacZb.setOpisstorno("Storniran prethodni dokument!");
        return this.stornoObr5(obrazacZb, user);
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private String stornoObr5(Integer id, String email) {
        User user = this.getUser(email);
        var obrazacZb = obrazacZbRepository.findById(id).get();
        obrazacZb.setRadna(0);
        obrazacZb.setStorno(1);
        obrazacZb.setStosifrad(user.getSifraradnika());
        obrazacZbRepository.save(obrazacZb);
        return "Obrazac5 uspesno je storniran";
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private String stornoObr5(ObrazacZb obrazacZb, User user) {
        obrazacZb.setRadna(0);
        obrazacZb.setStorno(1);
        obrazacZb.setStosifrad(user.getSifraradnika());
        obrazacZbRepository.save(obrazacZb);
        return "Obrazac5 uspesno je storniran";
    }

    private  Optional<ObrazacZb> findLastVersionOfObrazacZb(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacZbRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    private  Optional<ObrazacZb> findLastVersionOfObrazacZb(Integer jbbk, Integer kvartal) {
        return obrazacZbRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }
}
