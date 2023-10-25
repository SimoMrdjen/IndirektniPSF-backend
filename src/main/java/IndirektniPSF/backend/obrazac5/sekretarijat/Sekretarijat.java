package IndirektniPSF.backend.obrazac5.sekretarijat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sekretarijat")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Sekretarijat {

    @Id
    private Integer sif_sekret;

    @Column(name = "razdeo")
    private Integer razdeo;
    @Column
    private String NAZIV;
    @Column
    private Integer JED_BROJ_KORISNIKA;
    @Column
    private String MATICNI_BROJ;
    @Column
    private String PIB;
    @Column
    private String SEF;
    @Column
    private String TITULA;
    @Column
    private String POMOCNIK;
    @Column
    private Integer STARIRAZDEO;
    @Column
    private String PISMO;
    @Column
    private String SIFRA_DELATNOSTI;
    @Column
    private String MESTO;
    @Column
    private String ADRESA;
    @Column
    private String DNAZIV;
    @Column
    private Integer PTT;
    @Column
    private Integer AKTIVNO;
    @Column
    private Integer RBR_OKVIRA;
    @Column
    private Double DATUM_NEAKTIVNOSTI;
    @Column
    private String STARI_NAZIV;
    @Column
    private Double DATUM_IZM;
    @Column
    private Integer SIF_VRSTEORG;
}