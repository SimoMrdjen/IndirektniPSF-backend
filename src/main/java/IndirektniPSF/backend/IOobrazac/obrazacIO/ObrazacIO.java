package IndirektniPSF.backend.IOobrazac.obrazacIO;

import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.parameters.StatusUpdatable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obrazac5_pom_zb")
@AllArgsConstructor
@NoArgsConstructor
//@
@Getter
@Setter
@Builder
public class ObrazacIO implements StatusUpdatable {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer GEN_MYSQL;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "GEN_MYSQL")
    private List<ObrazacIODetails> stavke = new ArrayList<>();

    @Column
    private Integer GEN_INTERBASE;

    @Column(nullable = false)
    private Integer KOJI_KVARTAL;

    @Column(nullable = false)
    private Integer GODINA;

    @Column(nullable = false)
    private Integer VERZIJA;

    @Column(nullable = false)
    private Integer RADNA;

    @Column(nullable = false)
    private Integer SIF_SEKRET;

    @Column
    private Integer RAZDEO;

    @Column(nullable = false)
    private Integer JBBK;//jbbk sekretarijata

    @Column(nullable = false)
    private Integer JBBK_IND_KOR;

    @Column(nullable = false)
    private Integer SIF_RAC;

    @Column(nullable = false)
    private Integer DINARSKI;

    @Column(nullable = false)
    private Integer STATUS;

    @Column(nullable = false)
    private Integer POSLATO_O;

    @Column(nullable = false)
    private Integer POVUCENO;

    @Column(nullable = false)
    private Integer KONACNO;

    @Column
    private Integer POSLAO_NAM;

    @Column(nullable = false)
    private Integer DATUM_DOK;

    @Column
    private Integer PODIGAO_STATUS;

    @Column
    private Integer DATUM_POD_STATUSA;

    @Column
    private Integer POSLAO_U_ORG;

    @Column
    private Integer DATUM_SLANJA;

    @Column
    private Integer POSLAO_IZ_ORG;

    @Column
    private Integer DATUM_ORG;

    @Column
    private Integer ZAPRIMIO_VER;

    @Column
    private Integer OVERIO_VER;

    @Column
    private Integer ODOBRIO_VER;

    @Column(nullable = false)
    private Integer PROKNJIZENO;

    @Column(nullable = false)
    private Integer XLS;

    @Column(nullable = false)
    private Integer STORNO;

    @Column(nullable = false)
    private Integer STOSIFRAD;

    @Column
    private String OPISSTORNO;

    @Column(nullable = false)
    private Integer GEN_OPENTAB;

}
