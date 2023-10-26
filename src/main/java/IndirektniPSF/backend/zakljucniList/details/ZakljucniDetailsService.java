package IndirektniPSF.backend.zakljucniList.details;

import IndirektniPSF.backend.glavaSvi.GlavaSvi;
import IndirektniPSF.backend.glavaSvi.GlavaSviRepository;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
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
    private final SubkontoService subkontoService;
    private final GlavaSviService glavaSviService;
    //private final ExcelService excelService;

    @Transactional
    public List<ZakljucniListDetails> saveDetailsExcel(List<ZakljucniListDto> dtos, ZakljucniListZb zbSaved) throws Exception {

        //provera da li su ucitani samo postojeci 6-cifreni kontoi
       // this.checkIfKontosAreExisting(dtos);
        var jbbk = zbSaved.getJbbkIndKor();
        String oznakaGlave = glavaSviService.findGlava(jbbk);
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


}
