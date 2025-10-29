package IndirektniPSF.backend.obrazac5;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Obrazac5DTO {

    private Integer oznakOp;
    private Integer konto;
    private String opis;
    private Double planPrihoda;
    private Double izvrsenje;
    private Double republika;
    private Double pokrajina;
    private Double opstina;
    private Double ooso;
    private Double donacije;
    private Double ostali;
    public void setKonto(Integer konto) {
        this.konto = (konto != null) ? konto : 0;
    }

}
