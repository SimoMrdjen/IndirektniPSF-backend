package IndirektniPSF.backend.kontrole.poruka;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OBRAZAC_KONTROLA_PORUKA")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ObrazacKontrolaPoruka {

    @Id
    private Integer ID;

    private Integer ID_OBRAZAC_KONTROLA;

    private Integer ID_TIP_OBRASCA;

    private Integer RBR;

    private String OPIS;

    private String USLOV_VREDNOST;

    private String PORUKA;
}
