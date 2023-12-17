package IndirektniPSF.backend.arhbudzet;

import IndirektniPSF.backend.parameters.IfObrazacChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Component
public class ArhbudzetService implements IfObrazacChecker {

    private final ArhbudzetRepository repository;

    public  Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date, String glava, Integer jbbk) {

        return repository.sumUplataIzBudzetaForIndKor(sifSekr, date, jbbk);
    }
}
