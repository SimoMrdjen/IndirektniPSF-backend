package IndirektniPSF.backend.obrazac5;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Obrazac5DTO {

    private Integer prop1 ;
    private Integer prop2;
    private String prop3;
    private Double prop4;
    private Double prop5;
    private Double prop6;
    private Double prop7;
    private Double prop8;
    private Double prop9;
    private Double prop10;
    private Double prop11;

    public void setProp2(Integer prop2) {
        this.prop2 = (prop2 != null) ? prop2 : 0;
    }

}
