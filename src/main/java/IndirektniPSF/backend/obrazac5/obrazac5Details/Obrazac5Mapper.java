package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class Obrazac5Mapper {
    public Obrazac5details mapDtoToEntity(Obrazac5DTO dto) {
        var konto = (dto.getProp2() != null ? dto.getProp2() : 0);
        return Obrazac5details.builder()
                .verzija(1)
                .koji_kvartal(0)
                .sif_rac(1)
                .oznakaop(dto.getProp1())
                .konto(konto)
                .opis(dto.getProp3())
                .planprihoda(dto.getProp4())
                .republika(dto.getProp6())
                .pokrajina(dto.getProp7())
                .opstina(dto.getProp8())
                .ooso(dto.getProp9())
                .donacije(dto.getProp10())
                .ostali(dto.getProp11())
                .godplan(dto.getProp4())
                .izvrsenje(dto.getProp5())
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
                dto.setProp1(getIntegerValueFromCell(row.getCell(0)));
                dto.setProp2(getIntegerValueFromCell(row.getCell(1)));
                dto.setProp3(row.getCell(2).getStringCellValue());
                dto.setProp4(getDoubleValueFromCell(row.getCell(3)));
                dto.setProp5(getDoubleValueFromCell(row.getCell(4)));
                dto.setProp6(getDoubleValueFromCell(row.getCell(5)));
                dto.setProp7(getDoubleValueFromCell(row.getCell(6)));
                dto.setProp8(getDoubleValueFromCell(row.getCell(7)));
                dto.setProp9(getDoubleValueFromCell(row.getCell(8)));
                dto.setProp10(getDoubleValueFromCell(row.getCell(9)));
                dto.setProp11(getDoubleValueFromCell(row.getCell(10)));
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
                .build();
    }

}
