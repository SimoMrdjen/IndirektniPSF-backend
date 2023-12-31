package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.parameters.StatusUpdatable;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "OBRAZAC_ZAK_LIST_ZB")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ZakljucniListZb implements StatusUpdatable {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GEN_MYSQL")
    private Integer genMysql;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "GEN_MYSQL")
    private List<ZakljucniListDetails> stavke = new ArrayList<>();

    @Column
    private Integer GEN_INTERBASE = 0;

    @Column(nullable = false)
    private Integer GEN_OPENTAB = 0;

    @Column(nullable = false)
    private Integer GEN_APVDBK = 0;

    @Column(name = "KOJI_KVARTAL",nullable = false)
    private Integer kojiKvartal;

    @Column(nullable = false)
    private Integer GODINA;

    @Column(name = "VERZIJA", nullable = false)
    private Integer verzija;

    @Column(name = "RADNA", nullable = false)
    private Integer radna = 1;

    @Column(nullable = false)
    private Integer SIF_SEKRET;

    @Column(nullable = false)
    private Integer RAZDEO;

    @Column(nullable = false)
    private Integer JBBK;

    @Column(name = "JBBK_IND_KOR", nullable = false)
    private Integer jbbkIndKor;

    @Column(nullable = false)
    private Integer SIF_RAC = 1;

    @Column(nullable = false)
    private Integer DINARSKI = 1;

    @Column(nullable = false)
    private Integer STATUS = 0;

    @Column(nullable = false)
    private Integer POSLATO_O = 0;

    @Column(nullable = false)
    private Integer POVUCENO = 0;

    @Column(nullable = false)
    private Integer KONACNO = 0;

    private Integer POSLAO_NAM = 0;

    @Column(nullable = false)
    private Integer DATUM_DOK;

    private Integer PODIGAO_STATUS;

    private Integer DATUM_POD_STATUSA;

    private Integer POSLAO_U_ORG = 0;

    private Integer DATUM_SLANJA;

    private Integer POSLAO_IZ_ORG;

    private Integer DATUM_ORG;

    private Integer ZAPRIMIO_VER;

    private Integer OVERIO_VER;

    private Integer ODOBRIO_VER;

    @Column(nullable = false)
    private Integer PROKNJIZENO = 0;

    @Column(nullable = false)
    private Integer XLS = 0;

    @Column(nullable = false)
    private Integer STORNO = 0;

    @Column(nullable = false)
    private Integer STOSIFRAD = 0;

    private String OPISSTORNO;

    public Integer getRADNA() {
        return this.radna;
    }

    public void setRADNA(Integer radna) {
        this.radna = radna;
    }
}
