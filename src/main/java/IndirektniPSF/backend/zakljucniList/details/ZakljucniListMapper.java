package IndirektniPSF.backend.zakljucniList.details;

import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ValidOrStorno;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZakljucniListMapper {

    public ZakljucniListDetails mapDtoToEntity(ZakljucniListDto dto,
                                               ZakljucniListZb zb,
                                               String oznakaGlave) {
        Integer konto = Integer
                .parseInt(dto.getKonto().trim());

        return ZakljucniListDetails.builder()
                //.GEN_MYSQL(zb.getGenMysql())
                .zakljucniListZb(zb)
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
                .DUGUJE_PS(dto.getDugujePs())
                .POTRAZUJE_PS(dto.getPotrazujePs())
                .DUGUJE_PR(dto.getDugujePr())
                .POTRAZUJE_PR(dto.getPotrazujePr())
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
                dto.setKonto(row.getCell(0).getStringCellValue());
                dto.setDugujePs(row.getCell(1).getNumericCellValue());
                dto.setPotrazujePs(row.getCell(2).getNumericCellValue());
                dto.setDugujePr(row.getCell(3).getNumericCellValue());
                dto.setPotrazujePr(row.getCell(4).getNumericCellValue());

                zakljucniListDtos.add(dto);
                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani");
        }

        return zakljucniListDtos;
    }

    public ObrazacResponse toResponse(ZakljucniListZb zb) {
        LocalDate date = LocalDate.ofEpochDay(zb.getDATUM_DOK() - 25569);

        return ObrazacResponse.builder()
                .id(zb.getGenMysql())
                .date(date)
                .kvartal(zb.getKojiKvartal())
                .year(zb.getGODINA())
                .version(zb.getVerzija())
                .status(zb.getSTATUS())
                .jbbk(zb.getJbbkIndKor())
                .storno(zb.getSTORNO() == 0 ? ValidOrStorno.VALIDAN : ValidOrStorno.STORNIRAN)
                .build();
    }

    public ZakljucniListDto toDto(ZakljucniListDetails zld) {

        return ZakljucniListDto.builder()
                .konto(String.format("%06d", zld.getKONTO()))
                .dugujePs(zld.getDUGUJE_PS())
                .potrazujePs(zld.getPOTRAZUJE_PS())
                .dugujePr(zld.getDUGUJE_PR())
                .potrazujePr(zld.getPOTRAZUJE_PR())
                .build();
    }

}
