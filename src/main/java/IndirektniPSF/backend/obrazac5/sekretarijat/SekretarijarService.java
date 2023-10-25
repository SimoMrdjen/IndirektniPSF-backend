package IndirektniPSF.backend.obrazac5.sekretarijat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Component
public class SekretarijarService {

    private final SekretarijatRepository sekretarijatRepository;

    public Sekretarijat getSekretarijat(Integer sifSek) {
        Optional<Sekretarijat> sekretarijat = sekretarijatRepository.findById(sifSek);
        return sekretarijat.get();

    }
}
