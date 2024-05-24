package IndirektniPSF.backend.raspodela;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Component
public class RaspodelaService {

    private final RaspodelaRepository raspodelaRepository;

    public List<Raspodela> findDistinctByIzvorFinAndIbkIsOne() {
        return raspodelaRepository.findDistinctByIzvorFinAndIbkIsOne();
    }
}
