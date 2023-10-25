package IndirektniPSF.backend.kontrole.obrazac;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Component
public class ObrKontrService {

    private final ObrKontrRepository repository;

    public boolean isKontrolaMandatory(Integer id) {
        if (repository.findById(id).get().getSTROGA_PROVERA() == 0) {
            return false;
        }
        return true;
    }

    public boolean isKontrolaActive(Integer id) {
        if (repository.findById(id).get().getAKTIVNO() == 0) {
            return false;
        }
        return true;
    }




}
