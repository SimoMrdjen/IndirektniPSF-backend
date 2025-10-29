package IndirektniPSF.backend.krt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KrtService {
    private final KrtRepository repository;

    public List<Integer> findSifRacByUlaziUKrtAndDatumGasAndJedBrojKorisnika(Integer jedBrojKorisnika) {
        return repository.findSifRacByUlaziUKrtAndDatumGasAndJedBrojKorisnika(jedBrojKorisnika);
    }

}
