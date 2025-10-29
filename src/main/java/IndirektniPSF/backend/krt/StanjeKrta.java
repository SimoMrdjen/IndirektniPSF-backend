package IndirektniPSF.backend.krt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stanjekrta")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StanjeKrta {

    @Id
    private Integer rbr;

    @Column(name = "sif_rac")
    private Integer sifRac;

    private Double kumpot;

    private Integer izvod;
}
