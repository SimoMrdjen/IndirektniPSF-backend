package IndirektniPSF.backend.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStream;

@Service
public class ExcelService {

//    public Integer readCellByIndexes(InputStream inputStream, int rowIndex, int colIndex) throws Exception {
//
//        Integer cellValue = null;
//        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            Row row = sheet.getRow(rowIndex);
//
//            if (row != null) {
//                Cell cell = row.getCell(colIndex);
//                if (cell != null) {
//                    cellValue = (int) cell.getNumericCellValue();
//                }
//            }
//        } catch (Exception e) {
//            throw new Exception("Vrednost celije u redu " + rowIndex + "\ni koloni " + colIndex + " nije validna!");
//        }
//        return cellValue;
//    }



    public Integer readCellByIndexes(InputStream inputStream, int rowIndex, int colIndex) throws Exception {
        Integer cellValue = null;
        try (Workbook workbook = WorkbookFactory.create(inputStream)) { // Automatically determine the format
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(rowIndex);

            if (row != null) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            cellValue = (int) cell.getNumericCellValue();
                            break;
                        case STRING:
                            try {
                                cellValue = Integer.parseInt(cell.getStringCellValue());
                            } catch (NumberFormatException e) {
                                throw new Exception("Cannot convert string to integer in cell (" + rowIndex + "," + colIndex + ")");
                            }
                            break;
                        case BOOLEAN:
                            cellValue = cell.getBooleanCellValue() ? 1 : 0;
                            break;
                        case FORMULA:
                            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                            cellValue = (int) evaluator.evaluate(cell).getNumberValue();
                            break;
                        case BLANK:
                            throw new Exception("Cell (" + rowIndex + "," + colIndex + ") is blank.");
                        default:
                            throw new Exception("Unsupported cell type in (" + rowIndex + "," + colIndex + ")");
                    }
                } else {
                    throw new Exception("Cell (" + rowIndex + "," + colIndex + ") does not exist.");
                }
            } else {
                throw new Exception("Row " + rowIndex + " does not exist.");
            }
        } catch (IOException | InvalidFormatException e) {
            throw new Exception("Error reading Excel file: " + e.getMessage());
        }
        return cellValue;
    }


}
