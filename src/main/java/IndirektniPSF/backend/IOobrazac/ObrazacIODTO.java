package IndirektniPSF.backend.IOobrazac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObrazacIODTO {
    private Integer redBrojAkt;
    private String funkKlas;
    private Integer konto;
    private String izvorFin;
    private String izvorFinPre;
    private Double plan;
    private Double izvrsenje;
}
