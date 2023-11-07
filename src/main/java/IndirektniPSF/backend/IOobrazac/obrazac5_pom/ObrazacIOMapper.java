package IndirektniPSF.backend.IOobrazac.obrazac5_pom;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;

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

//        public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) throws IOException, InvalidFormatException {
//            List<ObrazacIODTO> dtos = new ArrayList<>();
//            DataFormatter formatter = new DataFormatter();
//
//            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
//                Sheet sheet = workbook.getSheetAt(0);
//                for (Row row : sheet) {
//                    // Skip rows before the actual data starts (header rows).
//                    if (row.getRowNum() < 7) {
//                        continue;
//                    }
//
//                    ObrazacIODTO dto = new ObrazacIODTO();
//
//                    // Iterate over the columns that are relevant for the DTO.
//                    for (int columnIndex = 0; columnIndex <= 6; columnIndex++) {
//                        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                        if (cell != null) {
//                            switch (cell.getCellType()) {
//                                case STRING:
//                                    assignStringValue(dto, columnIndex, cell.getStringCellValue());
//                                    break;
//                                case NUMERIC:
//                                    if (DateUtil.isCellDateFormatted(cell)) {
//                                        // Handle Date if required
//                                    } else {
//                                        assignNumericValue(dto, columnIndex, cell.getNumericCellValue());
//                                    }
//                                    break;
//                                // Additional cases (BOOLEAN, FORMULA, BLANK) could be handled if needed
//                            }
//                        }
//                    }
//
//                    dtos.add(dto);
//                }
//            } catch (Exception e) {
//                throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
//            }
//            return dtos;
//        }

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
                // Add more cases if there are more string columns
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
                // Add more cases if there are more numeric columns
            }
        }



//    private void assignStringValue(ObrazacIODTO dto, int columnIndex, String value) {
//        switch (columnIndex) {
//            case 1:
//                dto.setProp2(value);
//                break;
//            case 3:
//                dto.setProp4(value);
//                break;
//            case 4:
//                dto.setProp5(value);
//                break;
//        }
//    }
//
//    private void assignNumericValue(ObrazacIODTO dto, int columnIndex, double value) {
//        switch (columnIndex) {
//            case 0:
//                dto.setProp1((int) value);
//                break;
//            case 2:
//                dto.setProp3((int) value);
//                break;
//            case 5:
//                dto.setProp6(value);
//                break;
//            case 6:
//                dto.setProp7(value);
//                break;
//        }
//    }
//
    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {

        List<ObrazacIODTO> dtos = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
        Sheet sheet = workbook.getSheetAt(0);
        int i = 7;

//            while (true) {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    break;
//                }
            while (i <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break; // Stop reading when you find a null row
                }

                ObrazacIODTO dto = new ObrazacIODTO();

                Cell cell0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
                    dto.setProp1(Integer.parseInt(formatter.formatCellValue(cell0)));
                } else {
                    i++;
                    continue; // Skip this row if the required cell is blank or null
                }

//                dto.setProp1(Integer.parseInt(formatter.formatCellValue(row.getCell(0))));
                dto.setProp2(formatter.formatCellValue(row.getCell(1)));
                dto.setProp3(Integer.parseInt(formatter.formatCellValue(row.getCell(2))));
                dto.setProp4(formatter.formatCellValue(row.getCell(3)));
                dto.setProp5(formatter.formatCellValue(row.getCell(4)));
                dto.setProp6(row.getCell(5).getNumericCellValue());
                dto.setProp7(row.getCell(6).getNumericCellValue());


                    dtos.add(dto);
//                }
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
