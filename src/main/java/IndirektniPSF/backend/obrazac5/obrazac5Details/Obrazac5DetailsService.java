package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class Obrazac5DetailsService {

    private final Obrazac5DetailsRepository obrazac5DetailsRepository;
    private final Obrazac5Mapper obrazac5Mapper;

    @Transactional
    public List<Obrazac5details> saveDetailsExcel(List<Obrazac5DTO> dtos, Obrazac5 zb, ObrazacIO io) {
        List<Obrazac5details> obrazac5detailsList =
                dtos.stream()
                        .filter(dto -> dto.getKonto() % 1000 != 0)
                        .filter(dto -> dto.getPlanPrihoda() != 0 || dto.getIzvrsenje() != 0)//TODO check if it is mandatory
                        .map(obrazac5Mapper::mapDtoToEntity)
                        .toList();

        obrazac5detailsList
                .forEach(obrazac5details -> {
                    obrazac5details.setObrazac5(zb);
                    obrazac5details.setVerzija(zb.getVerzija());
                    obrazac5details.setKoji_kvartal(zb.getKoji_kvartal());
                    obrazac5details.setSif_sekret(zb.getSif_sekret());
                    obrazac5details.setRazdeo(zb.getRazdeo());
                    obrazac5details.setJbbk_ind_kor(zb.getJbbk_ind_kor());
                });
        return obrazac5DetailsRepository.saveAll(obrazac5detailsList);
    }
}
