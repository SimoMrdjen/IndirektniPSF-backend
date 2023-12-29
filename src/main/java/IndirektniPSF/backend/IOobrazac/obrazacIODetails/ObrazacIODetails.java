package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "obrazac5_pom")
@AllArgsConstructor
@NoArgsConstructor
//@Data
@Getter
@Setter
@Builder
public class ObrazacIODetails {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REDNI")
    private  Integer REDNI;

    @ManyToOne
//            ( cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name="GEN_MYSQL")
    private ObrazacIO obrazacIO;

    @Column
    private Integer GEN_INTERBASE;

    @Column(nullable = false)
    private Integer GODINA;

    @Column(nullable = false)
    private Integer VERZIJA;

    @Column(nullable = false)
    private Integer KOJI_KVARTAL;

    @Column(nullable = false)
    private Integer SIF_SEKRET;

    @Column(nullable = false)
    private Integer JBBK;

    @Column(nullable = false)
    private Integer JBBK_IND_KOR;

    @Column(nullable = false)
    private Integer SIF_RAC;

    @Column(nullable = false)
    private Integer RAZDEO;

    @Column
    private String OZNAKAGLAVE;

    @Column(nullable = false)
    private Integer RED_BROJ_AKT;

    @Column
    private String FUNK_KLAS;

    @Column(nullable = false)
    private Integer SIN_KONTO;

    @Column(nullable = false)
    private Integer KONTO;

    @Column
    private String IZVORFIN;

    @Column
    private String IZVORFIN_PRE;

    @Column(nullable = false)
    private Integer ALINEA;

    @Column
    private Double DUGG = 0.0;

    @Column
    private Double POTG = 0.0;

    @Column
    private Double DUGUJE = 0.0;

    @Column
    private Double POTRAZUJE = 0.0;

    @Column
    private Double REPUBLIKA;

    @Column
    private Double POKRAJINA;

    @Column
    private Double OPSTINA;

    @Column
    private Double OOSO;

    @Column
    private Double DONACIJE;

    @Column
    private Double OSTALI;

    @Column(nullable = false)
    private Integer UNOSIO;

    @Column(nullable = false)
    private Integer UPARENO;

    @Column
    private Double POTRAZUJE2;
}
