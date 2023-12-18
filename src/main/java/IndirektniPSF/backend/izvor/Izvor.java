package IndirektniPSF.backend.izvor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
public class Izvor {

    @Id
    private String IZVORFIN;
    private String kakva;
}
