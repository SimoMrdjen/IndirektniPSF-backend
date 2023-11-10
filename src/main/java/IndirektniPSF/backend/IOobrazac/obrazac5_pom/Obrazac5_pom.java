package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb.Obrazac5_pom_zb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "obrazac5_pom")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Obrazac5_pom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer REDNI;

    @ManyToOne( cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name="GEN_MYSQL")
    private Obrazac5_pom_zb obrazac5_pom_zb;

//    @Column(nullable = false)
//    private Integer GEN_MYSQL;

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
    private Double DUGG;

    @Column
    private Double POTG;

    @Column
    private Double DUGUJE;

    @Column
    private Double POTRAZUJE;

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
