package IndirektniPSF.backend.zakljucniList.details;

import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZakljucniListMapper {

    public ZakljucniListDetails mapDtoToEntity(ZakljucniListDto dto,
                                               ZakljucniListZb zb,
                                               String oznakaGlave) {
        Integer konto = Integer
                .parseInt(dto.getProp1().trim());
        return ZakljucniListDetails.builder()
                .GEN_MYSQL(zb.getGenMysql())
                .GEN_INTERBASE(0)
                .GEN_OPENTAB(zb.getGEN_OPENTAB())
                .GEN_APVDBK(zb.getGEN_APVDBK())
                .GODINA(zb.getGODINA())
                .VERZIJA(zb.getVerzija())
                .KOJI_KVARTAL(zb.getKojiKvartal())
                .SIF_SEKRET(zb.getSIF_SEKRET())
                .JBBK(zb.getJBBK())
                .JBBK_IND_KOR(zb.getJbbkIndKor())
                .SIF_RAC(zb.getSIF_RAC())
                .RAZDEO(zb.getRAZDEO())
                .OZNAKAGLAVE(oznakaGlave)
                .SIN_KONTO(konto / 100)
                .KONTO(konto)
                .RED_BROJ_AKT(0)
                .DUGUJE_PS(dto.getProp2())
                .POTRAZUJE_PS(dto.getProp3())
                .DUGUJE_PR(dto.getProp4())
                .POTRAZUJE_PR(dto.getProp5())
                .UNOSIO(zb.getPOSLAO_NAM())
                .build();
    }

    public List<ZakljucniListDto> mapExcelToPojo(InputStream inputStream) {
        List<ZakljucniListDto> zakljucniListDtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 7; // Start reading from the 6th row

            while (true) {
                Row row = sheet.getRow(i);

                if (row == null || row.getCell(0) == null ||
                        row.getCell(0).getStringCellValue().trim().isEmpty()) {
                    break; // Stop reading when you find a blank row
                }

                ZakljucniListDto dto = new ZakljucniListDto();
                dto.setProp1(row.getCell(0).getStringCellValue());
                dto.setProp2(row.getCell(1).getNumericCellValue());
                dto.setProp3(row.getCell(2).getNumericCellValue());
                dto.setProp4(row.getCell(3).getNumericCellValue());
                dto.setProp5(row.getCell(4).getNumericCellValue());

                zakljucniListDtos.add(dto);
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani");
        }

        return zakljucniListDtos;
    }

}
