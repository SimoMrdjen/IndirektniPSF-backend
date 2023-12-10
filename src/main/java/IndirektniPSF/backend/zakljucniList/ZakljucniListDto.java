package IndirektniPSF.backend.zakljucniList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZakljucniListDto {
    private String konto;
    private Double dugujePs = 0.0;
    private Double potrazujePs = 0.0;
    private Double dugujePr = 0.0;
    private Double potrazujePr = 0.0;
}

