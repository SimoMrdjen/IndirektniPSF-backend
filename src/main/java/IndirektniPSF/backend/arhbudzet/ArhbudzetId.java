package IndirektniPSF.backend.arhbudzet;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArhbudzetId implements Serializable {

    private Integer sifRac;
    private String brNaloga;
    private Long stavkaNal;

}
