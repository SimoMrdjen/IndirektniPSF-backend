package IndirektniPSF.backend.obrazac5.obrazac;

import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObrazacMapper {
    public Obrazac mapDtoToEntity(Obrazac5DTO dto) {
        return Obrazac.builder()
                .verzija(1)
                .koji_kvartal(0)
                .sif_rac(1)
                .oznakaop(dto.getProp1())
                .konto(dto.getProp2())
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
        //TODO set values of excel columns and first row
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 25; // Start reading from the 26th row (assuming 0-based index)

            while (true) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break; // Stop reading when you find a blank row
                }

                // Check if the row should be skipped based on the cell types
                if (isCellTypeNumericAndNotNull(row, 0) && isCellTypeNumericAndNotNull(row, 1) &&
                        isCellTypeNumeric(row, 3) && isCellTypeNumeric(row, 9)) {
                    Obrazac5DTO dto = new Obrazac5DTO();
                    dto.setProp1((int) row.getCell(0).getNumericCellValue());
                    dto.setProp2((int) row.getCell(1).getNumericCellValue());
                    dto.setProp3(row.getCell(2).getStringCellValue());
                    dto.setProp4(row.getCell(3).getNumericCellValue());
                    dto.setProp5(row.getCell(4).getNumericCellValue());
                    dto.setProp6(row.getCell(5).getNumericCellValue());
                    dto.setProp7(row.getCell(6).getNumericCellValue());
                    dto.setProp8(row.getCell(7).getNumericCellValue());
                    dto.setProp9(row.getCell(8).getNumericCellValue());
                    dto.setProp10(row.getCell(9).getNumericCellValue());
                    dto.setProp11(row.getCell(10).getNumericCellValue());
                    dto.setPropDuz((int) row.getCell(11).getNumericCellValue());

                    dtos.add(dto);
                }
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
        }
        return dtos;
    }

    private boolean isCellTypeNumeric(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if ( CellType.NUMERIC.equals(cell.getCellType())) {
            return true;
        }
        return false; // Skip the row if the cell is not numeric
    }
    private boolean isCellTypeNumericAndNotNull(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null && CellType.NUMERIC.equals(cell.getCellType())) {
            return true;
        }
        return false; // Skip the row if the cell is not numeric
    }
}
