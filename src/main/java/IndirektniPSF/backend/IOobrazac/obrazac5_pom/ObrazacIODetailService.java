package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.Obrazac5_pom_zb;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ObrazacIODetailService {

    private final ObrazacIOMapper obrazacMapper;
    private final Obrazac5_pomRepository obrazac5_pomRepository;
    private final GlavaSviService glavaSviService;


    @Transactional
    public void saveListOfObrazac5_pom(List<ObrazacIODTO> dtos, Obrazac5_pom_zb obrIOSaved) {

        Integer mysql = obrIOSaved.getGEN_MYSQL();
        Integer godina = obrIOSaved.getGODINA();
        Integer verzija = obrIOSaved.getVERZIJA();
        Integer kvartal = obrIOSaved.getKOJI_KVARTAL();
        var jbbk = obrIOSaved.getJBBK_IND_KOR();
        String oznakaGlave = glavaSviService.findGlava(jbbk);

        List<Obrazac5_pom> obrazacList =
        dtos.stream()
                .map(obrazacMapper::mapDtoToEntity)
               // .map(dto -> obrazacMapper.mapDtoToEntity(dto))
                .collect(Collectors.toList());

        obrazacList.forEach(obrazac -> {
            obrazac.setGEN_MYSQL(mysql);
            obrazac.setGODINA(godina);
            obrazac.setVERZIJA(verzija);
            obrazac.setKOJI_KVARTAL(kvartal);
            obrazac.setSIF_SEKRET(obrIOSaved.getSIF_SEKRET());
            obrazac.setJBBK(obrIOSaved.getJBBK());
            obrazac.setJBBK_IND_KOR(obrIOSaved.getJBBK_IND_KOR());
            obrazac.setSIF_RAC(obrIOSaved.getSIF_RAC());
            obrazac.setRAZDEO(obrIOSaved.getRAZDEO());
            obrazac.setOZNAKAGLAVE(oznakaGlave);
            obrazac.setUNOSIO(obrIOSaved.getPOSLAO_NAM());
        });
        obrazac5_pomRepository.saveAll(obrazacList);
    }
}
