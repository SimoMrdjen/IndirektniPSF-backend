package IndirektniPSF.backend.subkonto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Component
public class SubkontoService {

    private final SubkontoRepository repository;

    public List<Integer> getKontniPlan() {
        return repository.findAll()
                .stream()
                .map(Subkonto::getSubKonto)
                .collect(Collectors.toList());
    }
}
