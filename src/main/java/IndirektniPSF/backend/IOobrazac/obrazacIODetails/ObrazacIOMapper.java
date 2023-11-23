package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ValidOrStorno;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObrazacIOMapper {

    public ObrazacIODetails toEntity(ObrazacIODTO obrazacIODTO) {

        Integer konto = obrazacIODTO.getKonto();
        Double dugg =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getPlan() : 0;
        Double potg =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getPlan() : 0;
        Double duguje =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getIzvrsenje() : 0;
        Double potrazuje =
                (konto < 400000 && konto > 699999) ? obrazacIODTO.getIzvrsenje() : 0;
        return
        ObrazacIODetails.builder()
                .RED_BROJ_AKT(obrazacIODTO.getRedBrojAkt())
                .FUNK_KLAS(obrazacIODTO.getFunkKlas())
                .SIN_KONTO(konto / 100)
                .KONTO(konto)
                .IZVORFIN(obrazacIODTO.getIzvorFin())
                .IZVORFIN_PRE(obrazacIODTO.getIzvorFinPre())
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
                    dto.setFunkKlas(value);
                    break;
                case 3:
                    dto.setIzvorFin(value);
                    break;
                case 4:
                    dto.setIzvorFinPre(value);
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
                    dto.setRedBrojAkt(Integer.parseInt(formatter.formatCellValue(cell0)));
                } else {
                    i++;
                    continue;
                }
                dto.setFunkKlas(formatter.formatCellValue(row.getCell(1)));
                dto.setKonto(Integer.parseInt(formatter.formatCellValue(row.getCell(2))));
                dto.setIzvorFin(formatter.formatCellValue(row.getCell(3)));
                dto.setIzvorFinPre(formatter.formatCellValue(row.getCell(4)));
                dto.setPlan(row.getCell(5).getNumericCellValue());
                dto.setIzvrsenje(row.getCell(6).getNumericCellValue());
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
                .storno(zb.getSTORNO() == 0 ? ValidOrStorno.VALIDAN : ValidOrStorno.STORNIRAN)
                .build();
    }

    public ObrazacIODTO toDto(ObrazacIODetails ioDetails) {

        return ObrazacIODTO.builder()
                .redBrojAkt(ioDetails.getRED_BROJ_AKT())
                .funkKlas(ioDetails.getFUNK_KLAS())
                .konto(ioDetails.getKONTO())
                .izvorFin(ioDetails.getIZVORFIN())
                .izvorFinPre(ioDetails.getIZVORFIN_PRE())
                .plan(ioDetails.getDUGG() + ioDetails.getPOTG())
                .izvrsenje(ioDetails.getDUGUJE() + ioDetails.getPOTRAZUJE())
                .build();

    }
}
