package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;


import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom.ObrazacIODetailService;
import IndirektniPSF.backend.obrazac5.obrazacZb.ObrazacZbService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.AbParameterService;
import IndirektniPSF.backend.parameters.ObrazacService;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


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

    @Transactional
    public Obrazac5_pom_zb saveObrazacIO(List<ObrazacIODTO> dtos, Integer kvartal, Integer year, String email) {

        var user = userRepository.findByEmail(email).orElseThrow();

        Integer sifSekret = user.getZa_sif_sekret(); //fetch from table user-bice- user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret); //fetch from table user or sekr, im not sure
        Integer jbbk = pPartnerService.getJBBKS(user.getSifra_pp());
        Integer version = findVersion(jbbk, kvartal);
        Integer todayInt = (int) LocalDate.now().toEpochDay() + 25569;

        Obrazac5_pom_zb obrIO = Obrazac5_pom_zb.builder()
                .KOJI_KVARTAL(kvartal)
                .GODINA(year)
                .VERZIJA(version)
                .RADNA(1)
                .SIF_SEKRET(sifSekret)
                .RAZDEO(sekretarijat.getRazdeo())
                .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
                .JBBK_IND_KOR(jbbk)
                .SIF_RAC(1)
                .DINARSKI(1)
                .STATUS(0)
                .POSLATO_O(0)
                .POVUCENO(0)
                .KONACNO(0)
                .POSLAO_NAM(user.getSifraradnika())
                .DATUM_DOK(todayInt)
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

        obrazacIODetailService.saveListOfObrazac5_pom(dtos, obrIOSaved);
        return obrIOSaved;
    }

    private Integer findVersion(Integer jbbk, Integer kvartal) {
        Integer version = obrazacIOrepository.getLastVersionValue(jbbk, kvartal).orElse(0);
        return version + 1;
    }

    @Transactional
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

    private String stornoIO(Integer id, String email) {
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
        return obrazacIOrepository.findFirstByJBBK_IND_KORAndKOJI_KVARTALOrderByVERZIJADesc(jbbk, kvartal);
    }
}
