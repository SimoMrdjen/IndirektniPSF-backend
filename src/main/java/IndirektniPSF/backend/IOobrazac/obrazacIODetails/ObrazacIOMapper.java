package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObrazacIOMapper {

    public ObrazacIODetails mapDtoToEntity(ObrazacIODTO obrazacIODTO) {

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
        ObrazacIODetails.builder()
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

    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {

        List<ObrazacIODTO> dtos = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
        Sheet sheet = workbook.getSheetAt(0);
        int i = 7;
            while (i <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                ObrazacIODTO dto = new ObrazacIODTO();

                Cell cell0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
                    dto.setProp1(Integer.parseInt(formatter.formatCellValue(cell0)));
                } else {
                    i++;
                    continue;
                }
                dto.setProp2(formatter.formatCellValue(row.getCell(1)));
                dto.setProp3(Integer.parseInt(formatter.formatCellValue(row.getCell(2))));
                dto.setProp4(formatter.formatCellValue(row.getCell(3)));
                dto.setProp5(formatter.formatCellValue(row.getCell(4)));
                dto.setProp6(row.getCell(5).getNumericCellValue());
                dto.setProp7(row.getCell(6).getNumericCellValue());
                dtos.add(dto);
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
        }
        return dtos;
    }

    public ObrazacResponse toResponse(ObrazacIO zb) {
        LocalDate date = LocalDate.ofEpochDay(zb.getDATUM_DOK() - 25569);
        return ObrazacResponse.builder()
                .id(zb.getGEN_MYSQL())
                .date(date)
                .kvartal(zb.getKOJI_KVARTAL())
                //.year(zb.())
                .version(zb.getVERZIJA())
                .status(zb.getSTATUS())
                .jbbk(zb.getJBBK_IND_KOR())
                .build();
    }
}
