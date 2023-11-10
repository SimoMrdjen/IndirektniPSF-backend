package IndirektniPSF.backend.obrazac5.obrazac;

import IndirektniPSF.backend.IOobrazac.obrazac5_pom.Obrazac5_pom;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazacZb.ObrazacZb;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class ObrazacService {

    private final ObrazacRepository obrazacRepository;
    private final ObrazacMapper obrazacMapper;

    @Transactional
    public List<Obrazac> saveListObrazac(List<Obrazac5DTO> dtos, ObrazacZb zb) {

        List<Obrazac> obrazacList =
                dtos.stream()
                        .filter(dto -> dto.getProp2() % 1000 != 0)
                        .filter(dto -> (dto.getProp4() != 0 && dto.getProp5() != 0))
                        .map(obrazacMapper::mapDtoToEntity)
                        .collect(Collectors.toList());

        obrazacList
                .forEach(obrazac -> {
                    obrazac.setGen_mysql(zb.getGen_mysql());
                    obrazac.setVerzija(zb.getVerzija());
                    obrazac.setKoji_kvartal(zb.getKoji_kvartal());
                    obrazac.setSif_sekret(zb.getSif_sekret());
                    obrazac.setRazdeo(zb.getRazdeo());
                    obrazac.setJbbk_ind_kor(zb.getJbbk_ind_kor());
                });
        return obrazacRepository.saveAll(obrazacList);
    }

    @Transactional
    public List<Obrazac> saveDetailsExcel(List<Obrazac5DTO> dtos, ObrazacZb zb, List<Obrazac5_pom> stavke) {
        List<Obrazac> obrazacList =
                dtos.stream()
                        .filter(dto -> dto.getProp2() % 1000 != 0)
                        .map(obrazacMapper::mapDtoToEntity)
                        .collect(Collectors.toList());

        obrazacList
                .forEach(obrazac -> {
                    obrazac.setGen_mysql(zb.getGen_mysql());
                    obrazac.setVerzija(zb.getVerzija());
                    obrazac.setKoji_kvartal(zb.getKoji_kvartal());
                    obrazac.setSif_sekret(zb.getSif_sekret());
                    obrazac.setRazdeo(zb.getRazdeo());
                    obrazac.setJbbk_ind_kor(zb.getJbbk_ind_kor());
                });
        return obrazacRepository.saveAll(obrazacList);
    }
}
