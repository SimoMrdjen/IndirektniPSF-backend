package IndirektniPSF.backend.arhbudzet;

import IndirektniPSF.backend.parameters.IfObrazacChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Component
public class ArhbudzetService implements IfObrazacChecker {

    private final ArhbudzetRepository repository;

    public  Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date, String glava, Integer jbbk) {

        return repository.sumUplataIzBudzetaForIndKor(sifSekr, date, jbbk);
    }

    public List<Arhbudzet> findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(Integer jbbkInd, Integer kvartal) {
        Double datum = getLastDayOfKvartalAsDouble(kvartal);
        return repository.findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(jbbkInd, datum);
    }

    List<Arhbudzet> findByJbbkIndKorAndDatumLessThanEqualGroupByFields(Integer jbbkIndKor, Double datum) {
        return repository.findByJbbkIndKorAndDatumLessThanEqualGroupByFields(jbbkIndKor, datum);
    }
}
