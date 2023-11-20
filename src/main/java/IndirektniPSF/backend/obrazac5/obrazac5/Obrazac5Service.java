package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIORepository;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5Mapper;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5DetailsService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//--5--
@RequiredArgsConstructor
@Service
@Component
public class Obrazac5Service extends AbParameterService implements IfObrazacChecker, IfObrazacService<Obrazac5> {
    private final Obrazac5Repository obrazacRepository;
    private final SekretarijarService sekretarijarService;
    private final Obrazac5DetailsService obrazac5DetailsService;
    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
    private StringBuilder responseMessage =  new StringBuilder();
    private final Obrazac5Mapper mapper;
    private final ObrazacIORepository obrazacIOrepository;
    private final StatusService statusService;



    //--5--
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());



        User user = this.getUser(email);
        var jbbk = getJbbksIBK(user);

        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        ObrazacIO validIO = this. findValidIO(kvartal, jbbk);
//        checkDuplicatesKonta(dtos);
        Integer version = checkIfExistValidObrazac5AndFindVersion( jbbk, kvartal);

        List<Obrazac5DTO> dtos =mapper.mapExcelToPojo(file.getInputStream());

        Obrazac5 zb = Obrazac5.builder()
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
                .RADNA(1)
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

        Obrazac5 zbSaved = obrazacRepository.save(zb);
        var details =  obrazac5DetailsService.saveDetailsExcel(dtos, zbSaved, validIO);

        return responseMessage;
    }

    @Override
    public Obrazac5 findObrazacById(Integer id) {

       return obrazacRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Obrazac 5  ne postoji!"));
    }

    private ObrazacIO findValidIO(Integer kvartal, Integer jbbk) throws Exception {
        Optional<ObrazacIO> optionalZb =
                obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);

        if (optionalZb.isEmpty() || optionalZb.get().getRADNA() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Obrazac IO. Prvo ucitajte \n Obrazac IO!");
        }
        return optionalZb.get();
    }

    private ObrazacResponse findValidObr5ForStorno(String email, Integer kvartal) throws Exception {

        Integer jbbk = this.getJbbksIBK(email);
        Obrazac5 zb =
                obrazacRepository
                        .findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(jbbk, kvartal)
                        .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        return mapper.toResponse(zb);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidObrazac5AndFindVersion(Integer jbbks, Integer kvartal) throws ObrazacException {
        var optionalObrazacZb =
                this.findLastVersionOfObrazac5Zb(jbbks, kvartal);
        if (optionalObrazacZb.isEmpty()) {
        return 1;}
        Obrazac5 zb = optionalObrazacZb.get();
        checkIfExistValidObrazacYet(zb);
        return zb.getVerzija() + 1;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrAfterObrIO(User user, Integer kvartal) {

        Optional<Obrazac5> optionalObrazacZb = this.findLastVersionOfObrazac5Zb(user, kvartal);

        if (optionalObrazacZb.isEmpty())
            return "";

        var obrazacZb = optionalObrazacZb.get();
        if (obrazacZb.getRADNA() == 0 || obrazacZb.getSTORNO() == 1)
            return "";

        obrazacZb.setOpisstorno("Storniran prethodni dokument!");
        return this.stornoObr5(obrazacZb, user);
    }

    public String stornoObr5FromUser(Integer id, String email, Integer kvartal) {

      var zbForStorno = findObrazacById(id);
        User user = this.getUser(email);
       return stornoObr5(zbForStorno, user);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObr5(Integer id, String email) {
        User user = this.getUser(email);
        var obrazacZb = obrazacRepository.findById(id).get();
        obrazacZb.setRADNA(0);
        obrazacZb.setStorno(1);
        obrazacZb.setStosifrad(user.getSifraradnika());
        obrazacRepository.save(obrazacZb);
        return "Obrazac5 uspesno je storniran";
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObr5(Obrazac5 obrazac5, User user) {

        obrazac5.setRADNA(0);
        obrazac5.setStorno(1);
        obrazac5.setStosifrad(user.getSifraradnika());
        obrazacRepository.save(obrazac5);
        return "Obrazac5 uspesno je storniran";
    }

    private  Optional<Obrazac5> findLastVersionOfObrazac5Zb(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    private  Optional<Obrazac5> findLastVersionOfObrazac5Zb(Integer jbbk, Integer kvartal) {
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    private  Optional<Obrazac5> findLastVersionOfObrazac5Zb(String email, Integer kvartal) {

        var jbbk = this.getJbbksIBK(email);
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    public List<ObrazacResponse> findValidObrazacToStorno(String email, Integer kvartal) throws Exception {

        Obrazac5 zb = findLastVersionOfObrazac5Zb(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        isObrazacSentToDBK(zb);
        return List.of(mapper.toResponse(zb));
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email, Integer status, Integer kvartal) throws Exception {
        //TODO implement logic
        Obrazac5 zb = findLastVersionOfObrazac5Zb(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        isObrazacStorniran(zb);
        statusService.resolveObrazacAccordingStatus(zb, status);
        return List.of(mapper.toResponse(zb));
    }

    public String raiseStatus(Integer id, String email) throws Exception {

        User user = this.getUser(email);
        var zb = findObrazacById(id);
        this.checkStatusAndStorno(zb);
        return statusService.raiseStatusDependentOfActuallStatus(zb, user, obrazacRepository);
    }

}
