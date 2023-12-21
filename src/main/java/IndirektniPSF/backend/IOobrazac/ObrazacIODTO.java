package IndirektniPSF.backend.IOobrazac;

import IndirektniPSF.backend.izvor.Izvor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObrazacIODTO {
    private Integer redBrojAkt;
    private String funkKlas;
    private Integer konto;
    private String izvorFin;
    private String izvorFinPre;
    private Double plan;
    private Double izvrsenje;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObrazacIODTO that = (ObrazacIODTO) o;
        return Objects.equals(redBrojAkt, that.redBrojAkt) &&
                Objects.equals(funkKlas, that.funkKlas) &&
                Objects.equals(konto, that.konto) &&
                Objects.equals(izvorFin, that.izvorFin) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(redBrojAkt, funkKlas, konto, izvorFin);
    }

    @Override
    public String toString() {
        return "\nStand.klasif. :\n" +
                "program-akt: " + redBrojAkt +
                ", sin.konto: " + konto +
                "\n, izvor: " + izvorFin +
                ", funk.klasif: " + funkKlas + ".";
    }

//    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
//        List<ObrazacIODTO> dtos = new ArrayList<>();
//        DataFormatter formatter = new DataFormatter();
//        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int i = 7;
//            while (i <= sheet.getLastRowNum()) {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    break;
//                }
//                ObrazacIODTO dto = new ObrazacIODTO();
//
//                Cell cell0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
//                    dto.setRedBrojAkt(Integer.parseInt(formatter.formatCellValue(cell0)));
//                } else {
//                    i++;
//                    continue;
//                }
//                dto.setFunkKlas(formatter.formatCellValue(row.getCell(1)));
//
////                Cell kontoCell = row.getCell(2);
////                if (kontoCell != null) {
////                    if (kontoCell.getCellType() == CellType.STRING) {
////                        dto.setKonto(Integer.parseInt(kontoCell.getStringCellValue()));
////                    } else if (kontoCell.getCellType() == CellType.NUMERIC) {
////                        String formattedNumber = String.format("%06d", (int)kontoCell.getNumericCellValue());
////                        dto.setKonto(Integer.parseInt(formattedNumber));
////                    }
////                }
//               // dto.setKonto(row.getCell(0).getStringCellValue());
//
//
//                dto.setIzvorFin(formatter.formatCellValue(row.getCell(3)));
//                dto.setIzvorFinPre(formatter.formatCellValue(row.getCell(4)));
//
//                Cell planCell = row.getCell(5);
//                if (planCell != null && planCell.getCellType() == CellType.NUMERIC) {
//                    dto.setPlan(planCell.getNumericCellValue());
//                }
//
//                Cell izvrsenjeCell = row.getCell(6);
//                if (izvrsenjeCell != null && izvrsenjeCell.getCellType() == CellType.NUMERIC) {
//                    dto.setIzvrsenje(izvrsenjeCell.getNumericCellValue());
//                }
//
//                dtos.add(dto);
//                i++;
//            }
//        } catch (Exception e) {
//            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
//        }
//        return dtos;
//    }
}
