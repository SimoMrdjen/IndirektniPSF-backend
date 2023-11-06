package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObrazacIOMapper {

    public  Obrazac5_pom mapDtoToEntity(ObrazacIODTO obrazacIODTO) {

        Integer konto = obrazacIODTO.getProp3();
        Double dugg =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getProp6() : 0;
        Double potg =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getProp6() : 0;
        Double duguje =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getProp7() : 0;
        Double potrazuje =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getProp7() : 0;
        return
        Obrazac5_pom.builder()
                .RED_BROJ_AKT(obrazacIODTO.getProp1())
                .FUNK_KLAS(obrazacIODTO.getProp2())
                .SIN_KONTO(konto / 100)
                .KONTO(konto)
                .IZVORFIN(obrazacIODTO.getProp4())
                .IZVORFIN_PRE(obrazacIODTO.getProp5())
                .ALINEA(0)
                .DUGG(dugg)
                .POTG(potg)
                .DUGUJE(duguje)
                .POTRAZUJE(potrazuje)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .OOSO(0.0)
                .DONACIJE(0.0)
                .OSTALI(0.0)
                .UPARENO(0)
                .POTRAZUJE2(0.00)
                .build();
    }


    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
        List<ObrazacIODTO> dtos = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 7; // Start reading from the 8th row (assuming 0-based index)

            while (true) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break; // Stop reading when you find a blank row
                }

                ObrazacIODTO dto = new ObrazacIODTO();

                // Process each cell as per the cell type
                for (int columnIndex = 0; columnIndex <= 6; columnIndex++) {
                    Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    switch (cell.getCellType()) {
                        case STRING:
                            String stringValue = cell.getStringCellValue();
                            assignStringValue(dto, columnIndex, stringValue);
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                // Handle Date if required
                            } else {
                                double numericValue = cell.getNumericCellValue();
                                assignNumericValue(dto, columnIndex, numericValue);
                            }
                            break;
                        case BOOLEAN:
                            // Handle BOOLEAN if required
                            break;
                        case BLANK:
                            // Handle BLANK if required
                            break;
                        case FORMULA:
                            // Evaluate formula if required
                            break;
                        default:
                            // Handle other cases or throw an error
                    }
                }

                dtos.add(dto);
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
        }
        return dtos;
    }

    private void assignStringValue(ObrazacIODTO dto, int columnIndex, String value) {
        switch (columnIndex) {
            case 1:
                dto.setProp2(value);
                break;
            case 3:
                dto.setProp4(value);
                break;
            case 4:
                dto.setProp5(value);
                break;
        }
    }

    private void assignNumericValue(ObrazacIODTO dto, int columnIndex, double value) {
        switch (columnIndex) {
            case 0:
                dto.setProp1((int) value);
                break;
            case 2:
                dto.setProp3((int) value);
                break;
            case 5:
                dto.setProp6(value);
                break;
            case 6:
                dto.setProp7(value);
                break;
        }
    }

//    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
//        List<ObrazacIODTO> dtos = new ArrayList<>();
//        //TODO set values of excel columns and first row
//        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int i = 7; // Start reading from the 26th row (assuming 0-based index)
//
//            while (true) {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    break; // Stop reading when you find a blank row
//                }
//
//                // Check if the row should be skipped based on the cell types
////                if (isCellTypeNumericAndNotNull(row, 0))
////                {
//                    ObrazacIODTO dto = new ObrazacIODTO();
//                    dto.setProp1((int) row.getCell(0).getNumericCellValue());
//                    dto.setProp2(String.valueOf(row.getCell(1).getNumericCellValue()));
//                    dto.setProp3((int)row.getCell(2).getNumericCellValue());
//                    dto.setProp4(row.getCell(3).getStringCellValue());
//                    dto.setProp5(row.getCell(4).getStringCellValue());
//                    dto.setProp6(row.getCell(5).getNumericCellValue());
//                    dto.setProp7(row.getCell(6).getNumericCellValue());
//
//
//                    dtos.add(dto);
////                }
//                i++;
//            }
//        } catch (Exception e) {
//            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
//        }
//        return dtos;
//    }

//    private boolean isCellTypeNumeric(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        if ( CellType.NUMERIC.equals(cell.getCellType())) {
//            return true;
//        }
//        return false; // Skip the row if the cell is not numeric
//    }
    private boolean isCellTypeNumericAndNotNull(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null && CellType.NUMERIC.equals(cell.getCellType())) {
            return true;
        }
        return false; // Skip the row if the cell is not numeric
    }
}
