package IndirektniPSF.backend.subkonto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "subkonto")
public class Subkonto {

    @Id
    @Column(name = "sub_konto")
    private Integer subKonto;

    @Column(name = "evidencija_obv", nullable = false)
    private Integer evidencija_obv;

    @Column(name = "nazivsub_konto", nullable = false)
    private String nazivsub_konto;

    @Column(name = "kontostari")
    private Integer kontostari;

    @Column(name = "kontonovi")
    private Integer kontonovi;

    @Column(name = "sin_konto")
    private Integer sin_konto;

    @Column(name = "konto3")
    private Integer konto3;

    @Column(name = "analitika_kupaca", nullable = false)
    private Integer analitika_kupaca;

    @Column(name = "dnazivsub_konto")
    private String dnazivsub_konto;

    @Column(name = "aktivan", nullable = false)
    private Integer aktivan;

    @Column(name = "ide_ios", nullable = false)
    private Integer ide_ios;

    @Column(name = "vodi_radnik", nullable = false)
    private Integer vodi_radnik;
}
