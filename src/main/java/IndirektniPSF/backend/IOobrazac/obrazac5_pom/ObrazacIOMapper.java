package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import org.springframework.stereotype.Component;

@Component
public class ObrazacIOMapper {

    public  Obrazac5_pom mapDtoToEntity(ObrazacIODTO obrazacIODTO) {

        Integer konto = obrazacIODTO.getProp3();
        Double dugg =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getProp6() : 0;
        Double potg =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getProp6() : 0;
        Double duguje =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getProp7() : 0;
        Double potrazuje =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getProp7() : 0;
        return
        Obrazac5_pom.builder()
                .RED_BROJ_AKT(obrazacIODTO.getProp1())
                .FUNK_KLAS(obrazacIODTO.getProp2())
                .SIN_KONTO(konto / 100)
                .KONTO(konto)
                .IZVORFIN(obrazacIODTO.getProp4())
                .IZVORFIN_PRE(obrazacIODTO.getProp5())
                .ALINEA(0)
                .DUGG(dugg)
                .POTG(potg)
                .DUGUJE(duguje)
                .POTRAZUJE(potrazuje)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .OOSO(0.0)
                .DONACIJE(0.0)
                .OSTALI(0.0)
                .UPARENO(0)
                .POTRAZUJE2(0.00)
                .build();
    }
}
