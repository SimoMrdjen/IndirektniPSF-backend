package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5details;
import IndirektniPSF.backend.parameters.StatusUpdatable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obrazac_zb")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Obrazac5 implements StatusUpdatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gen_mysql;

    @OneToMany(mappedBy = "redni", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Obrazac5details> stavke = new ArrayList<>();

    @Column(name = "gen_interbase")
    private Integer gen_interbase;

    @Column(name = "koji_kvartal", nullable = false)
    private Integer koji_kvartal;

    @Column(name = "tip_obrazca", nullable = false)
    private Integer tip_obrazca;

    @Column(name = "sif_sekret", nullable = false)
    private Integer sif_sekret;

    @Column(name = "razdeo")
    private Integer razdeo;

    @Column(name = "sif_rac", nullable = false)
    private Integer sif_rac;

    @Column(name = "verzija", nullable = false)
    private Integer verzija;

    @Column(name = "dinarski", nullable = false)
    private Integer dinarski;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "poslato_o", nullable = false)
    private Integer poslato_o;

    @Column(name = "radna", nullable = false)
    private Integer radna;

    @Column(name = "povuceno", nullable = false)
    private Integer povuceno;

    @Column(name = "konacno", nullable = false)
    private Integer konacno;

    @Column(name = "poslao_nam")
    private Integer poslao_nam;

    @Column(name = "poslao_u_org")
    private Integer poslao_u_org;

    @Column(name = "poslao_iz_org")
    private Integer poslao_iz_org;

    @Column(name = "zaprimio_ver")
    private Integer zaprimio_ver;

    @Column(name = "overio_ver")
    private Integer overio_ver;

    @Column(name = "odobrio_ver")
    private Integer odobrio_ver;

    @Column(name = "proknjizeno", nullable = false)
    private Integer proknjizeno;

    @Column(name = "jbbk_ind_kor", nullable = false)
    private Integer jbbk_ind_kor;

    @Column(name = "storno", nullable = false)
    private Integer storno;

    @Column(name = "stosifrad")
    private Integer stosifrad;

    @Column(name = "opisstorno")
    private String opisstorno;

    @Column(name = "podigao_status")
    private Integer podigao_status;

    @Column(name = "datum_pod_statusa")
    private Integer datum_pod_statusa;

    @Column(name = "datum_org")
    private Integer datum_org;

    @Column(name = "nivo_konsolidacije", nullable = false)
    private Integer nivo_konsolidacije;

    @Override
    public Integer getSTATUS() {
        return status;
    }

    @Override
    public void setSTATUS(Integer status) {
        this.status = status;
    }

    @Override
    public void setPODIGAO_STATUS(Integer sifraradnika) {
        this.podigao_status = sifraradnika;

    }

    @Override
    public void setPOSLAO_NAM(Integer sifraradnika) {
        this.poslao_nam = sifraradnika;
    }

    @Override
    public Integer getSTORNO() {
        return this.storno;
    }
}
