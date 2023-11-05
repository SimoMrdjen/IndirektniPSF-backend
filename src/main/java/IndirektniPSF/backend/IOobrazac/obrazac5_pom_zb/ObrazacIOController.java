package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/obrazac_io")
@RequiredArgsConstructor
public class ObrazacIOController {

    private final ObrazacIOService obrazacIOService;

    @PostMapping(value = "/{kvartal}/{year}")
    public ResponseEntity<Obrazac5_pom_zb>  addObrazacIO(//@RequestHeader(value = "Authorization") String token,
                                                       @RequestBody List<ObrazacIODTO> dtos,
                                                       @PathVariable(name = "kvartal") Integer kvartal,
                                                       @PathVariable(name = "year") Integer year) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(obrazacIOService.saveObrazacIO(dtos, kvartal, year, email));

    }

    //TODO add endpoint for  excel
}
