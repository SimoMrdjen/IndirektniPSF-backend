package IndirektniPSF.backend.glavaSvi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GlavaSviService {

    private  final GlavaSviRepository repository;

    public String findGlava(Integer jbbk) {
        Optional<GlavaSvi> glavaSvi = repository.findByJedBrojKorisnikaAndAktivan(jbbk, 1);
        if(glavaSvi.isPresent()){
           return glavaSvi.get().getOznaka();
        }else{
            return "00";
        }
    }
}
