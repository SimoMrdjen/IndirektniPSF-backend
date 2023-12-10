package IndirektniPSF.backend.arhbudzet;

import IndirektniPSF.backend.izvor.Izvor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@IdClass(ArhbudzetId.class)
@Data
public class Arhbudzet {
    @Id
    @Column(name = "field1")
    private Integer field1;

    @Id
    @Column(name = "field2")
    private String field2;

    @Id
    @Column(name = "field3")
    private Long field3;

    private Double duguje;

    @Column(name = "sif_sekre")
    private Integer sifSekr;

    @Column(name = "sin_konto")
    private Integer sinKonto;

    private LocalDate datum;

    private Integer oznakaGlave;

    @Column(name = "jbbk_ind_kor")
    private Integer jbbkIndKor;

    @ManyToOne
    @JoinColumn(name = "izvorId")
    private Izvor izvor;


}
