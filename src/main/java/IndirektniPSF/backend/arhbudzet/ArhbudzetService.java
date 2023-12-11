package IndirektniPSF.backend.arhbudzet;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Component
public class ArhbudzetService {

    private final ArhbudzetRepository repository;

    public  Double sumDugujeForCriteria(Integer sifSekr, LocalDate date, Integer glava, Integer jbbk) {
        return repository.sumDugujeForCriteria(sifSekr, date, glava, jbbk);
    }
}
