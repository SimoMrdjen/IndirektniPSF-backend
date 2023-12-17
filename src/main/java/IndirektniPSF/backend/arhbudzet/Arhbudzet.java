package IndirektniPSF.backend.arhbudzet;

import IndirektniPSF.backend.izvor.Izvor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@IdClass(ArhbudzetId.class)
@Data
//@Table(name = "ARHBUDZET")
public class Arhbudzet {
    @Id
    @Column(name = "SIF_RAC")
    private Integer sifRac;

    @Id
    @Column(name = "BRNALOGA")
    private String brNaloga;

    @Id
    @Column(name = "STAVKANAL")
    private Long stavkaNal;

    @Column(name = "DUGUJE")
    private Double duguje;

    @Column(name = "SIF_SEKRET")
    private Integer sifSekr;

    @Column(name = "SIN_KONTO")
    private Integer sinKonto;

    @Column(name = "DATUM")
    private Double datum;
//    @Column(name = "DATUM")
//    private LocalDate datum;//DATUM

    @Column(name = "OZNAKAGLAVE")
    private String oznakaGlave;

    @Column(name = "JBBK_IND_KOR")
    private Integer jbbkIndKor;

    @ManyToOne
    @JoinColumn(name = "IZVORFIN")
    private Izvor izvor;

    @Column(name = "red_broj_akt")
    private Integer redBrojAkt;

    @Column(name = "funk_klas")
    private Integer funkKlas;
}
