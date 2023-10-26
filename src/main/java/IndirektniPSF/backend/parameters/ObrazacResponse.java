package IndirektniPSF.backend.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ObrazacResponse {
    @JsonProperty("id")
    Integer id;
    @JsonProperty("date")
    LocalDate date;
    @JsonProperty("kvartal")
    Integer kvartal;
    @JsonProperty("year")
    Integer year;
    @JsonProperty("version")
    Integer version;
    @JsonProperty("jbbk")
    Integer jbbk;
    @JsonProperty("status")
    Integer status;
    @JsonProperty("indirektni")
    Integer indirektni;
}
