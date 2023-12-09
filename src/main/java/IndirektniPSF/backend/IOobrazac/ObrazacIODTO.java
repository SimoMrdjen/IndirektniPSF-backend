package IndirektniPSF.backend.IOobrazac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObrazacIODTO that = (ObrazacIODTO) o;
        return Objects.equals(redBrojAkt, that.redBrojAkt) &&
                Objects.equals(funkKlas, that.funkKlas) &&
                Objects.equals(konto, that.konto) &&
                Objects.equals(izvorFin, that.izvorFin) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(redBrojAkt, funkKlas, konto, izvorFin, plan, izvrsenje);
    }
}
