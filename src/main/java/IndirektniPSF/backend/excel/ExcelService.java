package IndirektniPSF.backend.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ExcelService {


    public Integer readCellByIndexes(InputStream inputStream, int rowIndex, int colIndex) throws Exception {

        Integer cellValue = null;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(rowIndex);

            if (row != null) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    cellValue = (int) cell.getNumericCellValue();
                }
            }
        } catch (Exception e) {
            throw new Exception("Vrednost celije u redu " + rowIndex + "\ni koloni " + colIndex + " nije validna!");
        }
        return cellValue;
    }
}
