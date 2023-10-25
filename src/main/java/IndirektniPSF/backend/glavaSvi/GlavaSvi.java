package IndirektniPSF.backend.glavaSvi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "GLAVASVI")
public class GlavaSvi {



    @Column(name = "SIFRAGLAVE")
    private Integer sifraGlave;
    @Id
    @Column(name = "SIFRAGLAVES")
    private Integer sifraGlaves;

    @Column(name = "NAZIV")
    private String naziv;

    @Column(name = "DNAZIV")
    private String dNaziv;

    @Column(name = "OZNAKA")
    private String oznaka;

    @Column(name = "SIF_SEKRET")
    private Integer sifSekret;

    @Column(name = "JED_BROJ_KORISNIKA")
    private Integer jedBrojKorisnika;

    @Column(name = "TRANSFER")
    private Integer transfer;

    @Column(name = "AKTIVAN")
    private Integer aktivan;

}
