package IndirektniPSF.backend.review;

import IndirektniPSF.backend.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final AuthenticationService authenticationService;
    private final ReviewService reviewService;


    @GetMapping//(value = "/{kvartal}")
    public ResponseEntity<List<ObrazacResponse>> getActualObrasci(@RequestParam(name = "kvartal") Integer kvartal) {

        String email = authenticationService.getAuthenticatedUserEmail();
        return ResponseEntity
                .ok(reviewService.getActualObrasci(email, kvartal));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ObrazacResponse> getActualObrazac(@PathVariable(name = "id") Integer id,
                                                            @RequestParam(name = "kvartal") Integer kvartal,
                                                            @RequestParam(name = "typeOfObrazac") String typeOfObrazac) throws Exception {

        return ResponseEntity
                .ok(reviewService.getActualObrazacWithDetailsById(typeOfObrazac, id, kvartal));
    }
}
