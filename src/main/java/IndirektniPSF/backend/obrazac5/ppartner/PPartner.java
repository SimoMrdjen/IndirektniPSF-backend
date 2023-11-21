package IndirektniPSF.backend.obrazac5.ppartner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ppartner")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PPartner {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    private Integer sifra_pp;
    @Column
    private String partner;
    @Column
    private String pib;
    @Column
    private String adresa;
    @Column
    private Integer ptt;
    @Column
    private String mesto;
    @Column
    private Integer razdeo;
    @Column
    private Integer jbkbs;
    @Column
    private String jmbg;
    @Column
    private String pravnofiz;
    @Column
    private String bpg;
    @Column
    private String mbr;
    @Column
    private String telefon;
    @Column
    private String fax;
    @Column
    private String e_mail;
    @Column
    private String komentar;
    @Column
    private Integer sifradnika_uno;
    @Column
    private Double datum_uno;
    @Column
    private Double datum_izm;
    @Column
    private Integer sifradnika_izm;
    @Column
    private Integer sif_mesta;
    @Column
    private Integer javni_rac;
    @Column
    private String dnaziv;
    @Column
    private Integer sif_vrprim;
    @Column
    private Integer aktivan;
    @Column
    private Integer mesto_nep;
    @Column
    private Integer webprenos;
    @Column
    private Integer javno_pred;
    @Column
    private Integer open_prenos;
    @Column
    private String e_mail_pl;
}
