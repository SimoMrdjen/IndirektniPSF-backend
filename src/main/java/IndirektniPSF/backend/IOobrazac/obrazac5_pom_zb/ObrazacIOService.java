package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;


import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom.ObrazacIODetailService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.security.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Component
public class ObrazacIOService {
    private final ObrazacIORepository obrazacIOrepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final ObrazacIODetailService obrazacIODetailService;
    private final UserRepository userRepository;

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

    @Transactional
    private Integer findVersion(Integer jbbk, Integer kvartal) {
        Integer version = obrazacIOrepository.getLastVersionValue(jbbk, kvartal).orElse(0);
        return version + 1;
    }
}
