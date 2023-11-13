package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "obrazac")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Obrazac5details {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer redni;

    @ManyToOne( cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name="gen_mysql")
    private Obrazac5 obrazac5;

//    @Column(name = "gen_mysql")
//    private Integer gen_mysql;

    @Column(name = "gen_interbase")
    private Integer gen_interbase;

    @Column(name = "verzija")
    private Integer verzija;

    @Column(name = "koji_kvartal")
    private Integer koji_kvartal;

    @Column(name = "sif_sekret")
    private Integer sif_sekret;

    @Column(name = "sif_rac")
    private Integer sif_rac = 1;

    @Column(name = "razdeo")
    private Integer razdeo;

    @Column(name = "oznakaop")
    private Integer oznakaop;

    @Column(name = "dinarski")
    private Integer dinarski = 1;

    @Column(name = "konto")
    private Integer konto;

    @Column(name = "opis")
    private String opis;

    @Column(name = "planprihoda")
    private Double planprihoda;

    @Column(name = "republika")
    private Double republika;

    @Column(name = "pokrajina")
    private Double pokrajina;

    @Column(name = "opstina")
    private Double opstina;

    @Column(name = "ooso")
    private Double ooso;

    @Column(name = "donacije")
    private Double donacije;

    @Column(name = "ostali")
    private Double ostali;

    @Column(name = "godplan")
    private Double godplan;

    @Column(name = "kvplan")
    private Double kvplan = 0.0;

    @Column(name = "izvrsenje")
    private Double izvrsenje;

    @Column(name = "rep_b")
    private Double rep_b = 0.0;

    @Column(name = "pok_b")
    private Double pok_b = 0.0;

    @Column(name = "ops_b")
    private Double ops_b = 0.0;

    @Column(name = "ooso_b")
    private Double ooso_b = 0.0;

    @Column(name = "dona_b")
    private Double dona_b = 0.0;

    @Column(name = "ost_b")
    private Double ost_b = 0.0;

    @Column(name = "izvrs_bit")
    private Double izvrs_bit = 0.0;

    @Column(name = "izvrs_sop")
    private Double izvrs_sop = 0.0;

    @Column(name = "unosio")
    private Integer unosio;

    @Column(name = "za_unos")
    private Integer za_unos = 1;

    @Column(name = "tip_obrazca")
    private Integer tip_obrazca = 5;

    @Column(name = "jbbk_ind_kor")
    private Integer jbbk_ind_kor;

    @Column(name = "nivo_konsolidacije")
    private Integer nivo_konsolidacije = 0;

}
