package IndirektniPSF.backend.zakljucniList.details;

import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OBRAZAC_ZAK_LIST")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ZakljucniListDetails {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer REDNI;
    @Column(nullable = false)
    private Integer GEN_INTERBASE = 0;
    

    @ManyToOne
    @JoinColumn(name = "gen_mysql")
    private ZakljucniListZb zakljucniListZb;

    @Column(nullable = false)
    private Integer GEN_OPENTAB = 0;

    @Column(nullable = false)
    private Integer GEN_APVDBK = 0;

    @Column(nullable = false)
    private Integer GODINA = 0;

    @Column(nullable = false)
    private Integer VERZIJA = 0;

    @Column(nullable = false)
    private Integer KOJI_KVARTAL = 0;

    @Column(nullable = false)
    private Integer SIF_SEKRET = 0;

    @Column(nullable = false)
    private Integer JBBK = 0;

    @Column(nullable = false)
    private Integer JBBK_IND_KOR = 0;

    @Column(nullable = false)
    private Integer SIF_RAC = 0;

    @Column(nullable = false)
    private Integer RAZDEO = 0;

    private String OZNAKAGLAVE;

    @Column(nullable = false)
    private Integer RED_BROJ_AKT = 0;

    private String FUNK_KLAS;

    @Column(nullable = false)
    private Integer SIN_KONTO = 0;

    @Column(nullable = false)
    private Integer KONTO = 0;

    @Column(nullable = false)
    private Double DUGUJE_PS = 0.00;

    @Column(nullable = false)
    private Double POTRAZUJE_PS = 0.00;

    @Column(nullable = false)
    private Double DUGUJE_PR = 0.00;

    @Column(nullable = false)
    private Double POTRAZUJE_PR = 0.00;

    @Column(nullable = false)
    private Integer UNOSIO;

}
