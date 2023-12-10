package IndirektniPSF.backend.izvor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Izvor {


    @Id
    @GeneratedValue
    private Long izvorId;

    private String kakva;

}
