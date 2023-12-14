package IndirektniPSF.backend.arhbudzet;

import IndirektniPSF.backend.parameters.IfObrazacChecker;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Component
public class ArhbudzetService implements IfObrazacChecker {

    private final ArhbudzetRepository repository;

    public  Double sumDugujeForCriteria(Integer sifSekr, Double date, Integer glava, Integer jbbk) {

        return repository.sumDugujeForCriteria(sifSekr, date, glava, jbbk);
    }
}
