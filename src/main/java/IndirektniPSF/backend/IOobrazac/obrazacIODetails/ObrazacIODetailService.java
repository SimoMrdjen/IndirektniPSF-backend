package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.subkonto.SubkontoService;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
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
    private final ObrazacIODetailsRepository obrazacIODetailsRepository;
    private final SubkontoService subkontoService;


    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ObrazacIODetails> saveListOfObrazac5_pom(List<ObrazacIODTO> dtos,
                                                         ObrazacIO obrIOSaved,
                                                         List<ZakljucniListDetails> zakListDetails,
                                                         String oznakaGlave) throws Exception {

        //TODO include next control before deploying
        //this.checkIfKontosAreExisting(dtos)
        Integer godina = obrIOSaved.getGODINA();
        Integer verzija = obrIOSaved.getVERZIJA();
        Integer kvartal = obrIOSaved.getKOJI_KVARTAL();
        Integer jbbk = obrIOSaved.getJBBK_IND_KOR();

        List<ObrazacIODetails> obrazacList =
        dtos.stream()
                .map(obrazacMapper::toEntity)
               // .map(dto -> obrazacMapper.mapDtoToEntity(dto))
                .collect(Collectors.toList());

        obrazacList.forEach(obrazac -> {
            obrazac.setObrazacIO(obrIOSaved);
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
        var listDetails = obrazacIODetailsRepository.saveAll(obrazacList);
        this.compareIoDetailsWithZakListDetails(obrazacList, (List<ZakljucniListDetails>) zakListDetails);
       return listDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void compareIoDetailsWithZakListDetails(List<ObrazacIODetails> details, List<ZakljucniListDetails> stavke) throws Exception {
        if(false) {
            //TODO
            throw new Exception("Podaci sa dokumenta koji pokusavate \nda ucitate ne slazu se\n" +
                    "sa podacima sa vec ucitanog Zakljucnog lista!");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void checkIfKontosAreExisting(List<ObrazacIODTO> dtos) throws Exception {

        List<Integer> kontosInKontniPlan = subkontoService.getKontniPlan();
        List<Integer> nonExistingKontos = dtos.stream()
                .map(ObrazacIODTO::getKonto)
//                .map(kon -> kon.trim())
//                .map(Integer::parseInt)
                .filter((k) -> !kontosInKontniPlan.contains(k))
                .collect(Collectors.toList());

        List<String> nonExistingKontosString =  nonExistingKontos.stream()
                .map(konto -> Integer.toString(konto))
                .map(konto -> konto.length() < 6 ? ("0" + konto) : konto)
                .collect(Collectors.toList());

        if (!nonExistingKontos.isEmpty()) {
            throw new Exception("U obrascu listu postoje konta koja nisu" +
                    " \ndeo Kontnog plana: " + nonExistingKontosString);
        }
    }
}
