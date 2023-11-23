package IndirektniPSF.backend.review;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.review.ObrazacType;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    @JsonProperty("obrazacType")
    ObrazacType obrazacType;
    @JsonProperty("storno")
    ValidOrStorno storno;

    List<ZakljucniListDto> zakljucniListDtos;
    List<ObrazacIODTO> obrazacIODTOS;
    List<Obrazac5DTO> obrazac5DTOS;

}
