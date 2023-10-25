package IndirektniPSF.backend.zakljucniList.details;

import IndirektniPSF.backend.glavaSvi.GlavaSvi;
import IndirektniPSF.backend.glavaSvi.GlavaSviRepository;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.subkonto.SubkontoService;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZakljucniDetailsService {

    private final ZakljucniListMapper mapper;
    private final ZakljucniDetailsRepository zakljucniDetailsRepository;
    private final GlavaSviRepository glaviSviRepository;
    private final SubkontoService subkontoService;
    private final ObrKontrService obrKontrService;
    //private final ExcelService excelService;

//    @Transactional
//    public List<ZakljucniListDetails> saveDetails(List<ZakljucniListDto> dtos, ZakljucniListZb zbSaved) throws Exception {
//        //provera da li su ucitani samo postojeci 6-cifreni kontoi
//        this.checkIfKontosAreExisting(dtos);
//
//        var jbbk = zbSaved.getJbbkIndKor();
//        String oznakaGlave;
//
//        Optional<GlavaSvi> glavaSvi = glaviSviRepository.findByJedBrojKorisnika(jbbk);
//        if(glavaSvi.isPresent()){
//            oznakaGlave = glavaSvi.get().getOznaka();
//        }else{
//            oznakaGlave = "00";}
//
//        List<ZakljucniListDetails> details = dtos.stream()
//                .map(d -> mapper.mapDtoToEntity(d, zbSaved, oznakaGlave))
//                .collect(Collectors.toList());
//
//        return zakljucniDetailsRepository.saveAll(details);
//    }

    @Transactional
    public List<ZakljucniListDetails> saveDetailsExcel(List<ZakljucniListDto> dtos, ZakljucniListZb zbSaved) throws Exception {


        //provera da li su ucitani samo postojeci 6-cifreni kontoi
       // this.checkIfKontosAreExisting(dtos);

        var jbbk = zbSaved.getJbbkIndKor();
        String oznakaGlave;

        Optional<GlavaSvi> glavaSvi = glaviSviRepository.findByJedBrojKorisnikaAndAktivan(jbbk, 1);
        if(glavaSvi.isPresent()){
            oznakaGlave = glavaSvi.get().getOznaka();
        }else{
            oznakaGlave = "00";}

        List<ZakljucniListDetails> details = dtos.stream()
                .map(d -> mapper.mapDtoToEntity(d, zbSaved, oznakaGlave))
                .collect(Collectors.toList());

        return zakljucniDetailsRepository.saveAll(details);
    }
    public void checkIfKontosAreExisting(List<ZakljucniListDto> dtos) throws Exception {

        List<Integer> kontosInKontniPlan = subkontoService.getKontniPlan();

        List<Integer> nonExistingKontos = dtos.stream()
                .map(ZakljucniListDto::getProp1)
                .map(kon -> kon.trim())
                .map(Integer::parseInt)
                .filter((k) -> !kontosInKontniPlan.contains(k))
                .collect(Collectors.toList());

        List<String> nonExistingKontosString =  nonExistingKontos.stream()
                .map(konto -> Integer.toString(konto))
                .map(konto -> konto.length() < 6 ? ("0" + konto) : konto)
                .collect(Collectors.toList());

        if (!nonExistingKontos.isEmpty()) {
            throw new Exception("U Zakljucnom listu postoje konta koja nisu \ndeo Kontnog plana: " + nonExistingKontosString);
        }
    }

//    public String checkDuplicatesKonta(List<ZakljucniListDto> dtos) throws Exception {
//
//        String result = "";
//        var validError = obrKontrService.isKontrolaMandatory(9);
//        var isKontrolaActive = obrKontrService.isKontrolaActive(9);
//
//        List<String> kontos =
//                dtos.stream()
//                        .map(dto -> dto.getProp1().trim())
//                        .collect(Collectors.toList());
//
//        List<String> duplicates = kontos.stream()
//                .collect(Collectors.toMap(
//                        e -> e,
//                        v -> 1,
//                        (existing, replacement) -> existing + replacement))
//                .entrySet()
//                .stream()
//                .filter(entry -> entry.getValue() > 1)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        if (isKontrolaActive) {
//            if (!duplicates.isEmpty() && validError) {
//                throw new Exception("Imate duplirana konta: " + duplicates);
//            }
//            else if (!duplicates.isEmpty() && !validError) {
//               result = ("Imate duplirana konta: " + duplicates);
//            }
//        }
//        return result;
//    }

//    public List<ZakljucniListDto> mapExcelToPojo(InputStream inputStream) {
//        List<ZakljucniListDto> zakljucniListDtos = new ArrayList<>();
//
//        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int i = 5; // Start reading from the 6th row
//
//            while (true) {
//                Row row = sheet.getRow(i);
//
//                if (row == null || row.getCell(0) == null ||
//                        row.getCell(0).getStringCellValue().trim().isEmpty()) {
//                    break; // Stop reading when you find a blank row
//                }
//
//                ZakljucniListDto dto = new ZakljucniListDto();
//                dto.setProp1(row.getCell(0).getStringCellValue());
//                dto.setProp2(row.getCell(1).getNumericCellValue());
//                dto.setProp3(row.getCell(2).getNumericCellValue());
//                dto.setProp4(row.getCell(3).getNumericCellValue());
//                dto.setProp5(row.getCell(4).getNumericCellValue());
//
//                zakljucniListDtos.add(dto);
//                i++;
//            }
//        } catch (Exception e) {
//            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani");
//        }
//
//        return zakljucniListDtos;
//    }
}
