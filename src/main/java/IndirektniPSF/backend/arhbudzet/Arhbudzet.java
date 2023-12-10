package IndirektniPSF.backend.arhbudzet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(ArhbudzetId.class)
public class Arhbudzet {
    @Id
    @Column(name = "field1")
    private Integer field1;

    @Id
    @Column(name = "field2")
    private String field2;

    @Id
    @Column(name = "field3")
    private Long field3;
}
