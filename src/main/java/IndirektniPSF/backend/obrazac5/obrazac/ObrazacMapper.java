package IndirektniPSF.backend.obrazac5.obrazac;

import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import org.springframework.stereotype.Component;

@Component
public class ObrazacMapper {
    public Obrazac mapDtoToEntity(Obrazac5DTO dto) {
        return Obrazac.builder()
                .verzija(1)
                .koji_kvartal(0)
                .sif_rac(1)

                .oznakaop(dto.getProp1())
                .konto(dto.getProp2())
                .opis(dto.getProp3())
                .planprihoda(dto.getProp4())
                .republika(dto.getProp6())
                .pokrajina(dto.getProp7())
                .opstina(dto.getProp8())
                .ooso(dto.getProp9())
                .donacije(dto.getProp10())
                .ostali(dto.getProp11())
                .godplan(dto.getProp4())
                .izvrsenje(dto.getProp5())
                .unosio(0)

                .dinarski(1)
                .kvplan(0.0)
                .rep_b(0.0)
                .pok_b(0.0)
                .ops_b(0.0)
                .ooso_b(0.0)
                .dona_b(0.0)
                .ost_b(0.0)
                .izvrs_bit(0.0)
                .izvrs_sop(0.0)
                .za_unos(1)
                .tip_obrazca(5)
                .nivo_konsolidacije(0)
                .build();
    }
}
