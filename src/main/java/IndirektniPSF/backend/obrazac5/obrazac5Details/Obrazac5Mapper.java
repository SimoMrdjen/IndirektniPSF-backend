package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ValidOrStorno;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Obrazac5Mapper {
    public Obrazac5details mapDtoToEntity(Obrazac5DTO dto) {
        var konto = (dto.getKonto() != null ? dto.getKonto() : 0);
        return Obrazac5details.builder()
                .verzija(1)
                .koji_kvartal(0)
                .sif_rac(1)
                .oznakaop(dto.getOznakOp())
                .konto(konto)
                .opis(dto.getOpis())
                .planprihoda(dto.getPlanPrihoda())
                .republika(dto.getRepublika())
                .pokrajina(dto.getPokrajina())
                .opstina(dto.getOpstina())
                .ooso(dto.getOoso())
                .donacije(dto.getDonacije())
                .ostali(dto.getOstali())
                .godplan(dto.getPlanPrihoda())
                .izvrsenje(dto.getIzvrsenje())
                .unosio(0)
                .dinarski(1)
                .kvplan(0.0)
                .rep_b(0.0)
                .pok_b(0.0)
                .ops_b(0.0)
                .ooso_b(0.0)
                .dona_b(0.0)
                .ost_b(0.0)
                .izvrs_bit(0.0)
                .izvrs_sop(0.0)
                .za_unos(1)
                .tip_obrazca(5)
                .nivo_konsolidacije(0)
                .build();
    }

    public List<Obrazac5DTO> mapExcelToPojo(InputStream inputStream) {
        List<Obrazac5DTO> dtos = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 25; // Start reading from the 26th row (assuming 0-based index)
            int consecutiveBlankRows = 0;
            int allowedBlankRows = 3; // End reading after 3 blank rows

            while (consecutiveBlankRows < allowedBlankRows) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    consecutiveBlankRows++;
                    i++;
                    continue;
                } else {
                    consecutiveBlankRows = 0;
                }
                Obrazac5DTO dto = new Obrazac5DTO();
                dto.setOznakOp(getIntegerValueFromCell(row.getCell(0)));
                dto.setKonto(getIntegerValueFromCell(row.getCell(1)));
                dto.setOpis(row.getCell(2).getStringCellValue());
                dto.setPlanPrihoda(getDoubleValueFromCell(row.getCell(3)));
                dto.setIzvrsenje(getDoubleValueFromCell(row.getCell(4)));
                dto.setRepublika(getDoubleValueFromCell(row.getCell(5)));
                dto.setPokrajina(getDoubleValueFromCell(row.getCell(6)));
                dto.setOpstina(getDoubleValueFromCell(row.getCell(7)));
                dto.setOoso(getDoubleValueFromCell(row.getCell(8)));
                dto.setDonacije(getDoubleValueFromCell(row.getCell(9)));
                dto.setOstali(getDoubleValueFromCell(row.getCell(10)));
                dtos.add(dto);
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
        }
        return dtos;
    }

    private Integer getIntegerValueFromCell(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : null;
    }

    private Double getDoubleValueFromCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return 0.00; // Return 0.00 if the cell is null or not numeric
        } else {
            return cell.getNumericCellValue(); // Return the cell's value if it is numeric
        }
    }

    public ObrazacResponse toResponse(Obrazac5 zb) {
        LocalDate date = LocalDate.ofEpochDay(zb.getDatum_org() - 25569);
        return ObrazacResponse.builder()
                .id(zb.getGen_mysql())
                .date(date)
                .kvartal(zb.getKoji_kvartal())
                //.year(zb.())
                .version(zb.getVerzija())
                .status(zb.getSTATUS())
                .jbbk(zb.getJbbk_ind_kor())
                .storno(zb.getSTORNO() == 0 ? ValidOrStorno.VALIDAN : ValidOrStorno.STORNIRAN)
                .build();
    }

    public Obrazac5DTO toDto(Obrazac5details obrazac5details) {
        return Obrazac5DTO.builder()
                .oznakOp(obrazac5details.getOznakaop())
                .konto(obrazac5details.getKonto())
                .opis(obrazac5details.getOpis())
                .planPrihoda(obrazac5details.getPlanprihoda())
                .izvrsenje(obrazac5details.getIzvrsenje())
                .republika(obrazac5details.getRepublika())
                .pokrajina(obrazac5details.getPokrajina())
                .opstina(obrazac5details.getOpstina())
                .ooso(obrazac5details.getOoso())
                .donacije(obrazac5details.getDonacije())
                .ostali(obrazac5details.getOstali())
                .build();
    }

    public List<Obrazac5details> mapIOtoObr5(List<ObrazacIODetails> stavke) {
        var obr5FromIO = stavke.stream().map( st ->
                (Obrazac5details.builder()
                        .konto(st.getSIN_KONTO() * 100)
                        .republika(st.getREPUBLIKA())
                        .pokrajina(st.getPOKRAJINA())
                        .opstina(st.getOPSTINA())
                        .ooso(st.getOOSO())
                        .donacije(st.getDONACIJE())
                        .ostali(st.getOSTALI())
                        .build())
        ).collect(Collectors.toList());
        return aggregateByKonto(obr5FromIO);
    }

    public List<Obrazac5details> aggregateByKonto(List<Obrazac5details> detailsList) {
        return detailsList.stream()
                .collect(Collectors.groupingBy(
                        Obrazac5details::getKonto, // Group by 'konto'
                        Collectors.reducing(new Obrazac5details(), (a, b) -> {
                            Obrazac5details result = new Obrazac5details();
                            result.setKonto(a.getKonto() != null ? a.getKonto() : b.getKonto());
                            result.setPokrajina((a.getPokrajina() != null ? a.getPokrajina() : 0.0) +
                                    (b.getPokrajina() != null ? b.getPokrajina() : 0.0));
                            result.setRepublika((a.getRepublika() != null ? a.getRepublika() : 0.0) +
                                    (b.getRepublika() != null ? b.getRepublika() : 0.0));
                            result.setOpstina((a.getOpstina() != null ? a.getOpstina() : 0.0) +
                                    (b.getOpstina() != null ? b.getOpstina() : 0.0));
                            result.setOoso((a.getOoso() != null ? a.getOoso() : 0.0) +
                                    (b.getOoso() != null ? b.getOoso() : 0.0));
                            result.setDonacije((a.getDonacije() != null ? a.getDonacije() : 0.0) +
                                    (b.getDonacije() != null ? b.getDonacije() : 0.0));
                            result.setOstali((a.getOstali() != null ? a.getOstali() : 0.0) +
                                    (b.getOstali() != null ? b.getOstali() : 0.0));
                            return result;
                        })
                ))
                .values()
                .stream()
                .toList();
    }
}
