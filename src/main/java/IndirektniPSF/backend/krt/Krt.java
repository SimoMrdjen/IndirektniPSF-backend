package IndirektniPSF.backend.krt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "krt")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Krt {

    @Id
    @Column(name = "sif_rac")
    private  Integer sifRac;

    @Column(name = "ulazi_u_krt")
    private Integer ulaziUKrt;

    @Column(name = "DATUM_GAS")
    private Integer datumGas;

    @Column(name = "jed_broj_korisnika")
    private Integer jedBrojKorisnika;
}
