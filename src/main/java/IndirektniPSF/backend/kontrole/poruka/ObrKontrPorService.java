package IndirektniPSF.backend.kontrole.poruka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Component
public class ObrKontrPorService {
    private final ObrKontrPorRepository repository;

}
