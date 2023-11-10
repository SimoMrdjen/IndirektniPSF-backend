package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.Obrazac5_pom_zb;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ObrazacIODetailService {

    private final ObrazacIOMapper obrazacMapper;
    private final Obrazac5_pomRepository obrazac5_pomRepository;
    private final GlavaSviService glavaSviService;


    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Obrazac5_pom> saveListOfObrazac5_pom(List<ObrazacIODTO> dtos,
                                                     Obrazac5_pom_zb obrIOSaved,
                                                     List<ZakljucniListDetails> zakListDetails) throws Exception {

        //Integer mysql = obrIOSaved.getGEN_MYSQL();
        Integer godina = obrIOSaved.getGODINA();
        Integer verzija = obrIOSaved.getVERZIJA();
        Integer kvartal = obrIOSaved.getKOJI_KVARTAL();
        Integer jbbk = obrIOSaved.getJBBK_IND_KOR();
        String oznakaGlave = glavaSviService.findGlava(jbbk);

        List<Obrazac5_pom> obrazacList =
        dtos.stream()
                .map(obrazacMapper::mapDtoToEntity)
               // .map(dto -> obrazacMapper.mapDtoToEntity(dto))
                .collect(Collectors.toList());

        obrazacList.forEach(obrazac -> {
            obrazac.setObrazac5_pom_zb(obrIOSaved);
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
        var listDetails = obrazac5_pomRepository.saveAll(obrazacList);
        this.compareIoDetailsWithZakListDetails(obrazacList,zakListDetails);
       return listDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void compareIoDetailsWithZakListDetails(List<Obrazac5_pom> details, List<ZakljucniListDetails> stavke) throws Exception {
        if(false) {
            //TODO
            throw new Exception("Podaci sa dokumenta koji pokusavate \nda ucitate ne slazu se\n" +
                    "sa podacima sa vec ucitanog Zakljucnog lista!");
        }
    }
}
