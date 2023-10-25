package IndirektniPSF.backend.kontrole.obrazac;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OBRAZAC_KONTROLA")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ObrazacKontrola {
    @Id
    private Integer ID;

    private Integer ID_TIP_OBRASCA;

    private String OPIS;

    private String SQL_UPIT;

    private String NAZIV_MODULA;

    private Integer RBR_STAVKE;

    private Integer STROGA_PROVERA;

    private Integer AKTIVNO;

    private Integer REDNI;

}
