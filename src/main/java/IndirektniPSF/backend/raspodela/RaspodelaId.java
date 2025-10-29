package IndirektniPSF.backend.raspodela;

import lombok.Data;

import java.io.Serializable;


@Data
public class RaspodelaId implements Serializable {

    private String izvorFin;
    private Integer kolona;
}
