package IndirektniPSF.backend.IOobrazac;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PomObrazac {
    private Integer konto;
    private Double saldo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PomObrazac that = (PomObrazac) o;
        return Objects.equals(konto, that.konto) &&
                Objects.equals(saldo, that.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konto, saldo);
    }

}
