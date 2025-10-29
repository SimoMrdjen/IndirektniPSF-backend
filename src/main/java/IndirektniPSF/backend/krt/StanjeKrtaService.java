package IndirektniPSF.backend.krt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StanjeKrtaService {

    private final KrtService krtService;
    private final StanjeKrtaRepository repository;

    public Double getTransferedAmountOfBalancesforJbbk(Integer jbbks) {

        List<Integer> accounts = krtService.findSifRacByUlaziUKrtAndDatumGasAndJedBrojKorisnika(jbbks);
        return repository.transferedAmountOfBalancesforJbbk(accounts).orElse(0.00);
    }

}
