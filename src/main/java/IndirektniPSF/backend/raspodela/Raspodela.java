package IndirektniPSF.backend.raspodela;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(RaspodelaId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "obrazac5_izvor")
public class Raspodela {
    @Id
    @Column(name = "IZVORFIN")
    private String izvorFin;

    @Id
    @Column(name = "KOLONA")
    private Integer kolona;

    @Column(name = "SIFIZV")
    private Integer sifIzv;

    @Column(name = "IBK")
    private Integer ibk;

    @Column(name = "REDOSLED")
    private Integer redosled;
}