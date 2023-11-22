package IndirektniPSF.backend.review;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5Service;
import IndirektniPSF.backend.parameters.AbParameterService;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListMapper;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Component
public class ReviewService extends AbParameterService {

    private final ObrazacIOService obrazacIoService;
    private final ZakljucniListZbService zakljucniListService;
    private final Obrazac5Service obrazac5Service;


    public List<ObrazacResponse> getActualObrasci(String email, Integer kvartal) {

        Integer jbbks = getJbbksIBK(email);
     return List.of(
             zakljucniListService.getZakListResponse(jbbks, kvartal),
             obrazacIoService.obrazacIOForResponse(jbbks,kvartal),
             obrazac5Service.obrazac5ForResponse(jbbks, kvartal)
     );
    }

}
