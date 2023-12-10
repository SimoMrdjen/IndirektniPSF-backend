package IndirektniPSF.backend.IOobrazac.obrazacIO;

import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/obrazac_io")
@RequiredArgsConstructor
public class ObrazacIOController {

    private final ObrazacIOService obrazacIOService;
    private final FileUploadService fileUploadService;
    private final AuthenticationService authenticationService;


    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<String> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
        String message = null;
        try {
            message = String.valueOf(obrazacIOService.saveObrazacFromExcel(file, kvartal, email));
        }
        catch (Exception e) {
            message = e.getMessage();
            throw e;
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"Obrazac_IO", file, message, null);
        }
        return ResponseEntity.ok(message);
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<String> raiseStatus(@PathVariable(name = "id") Integer id,
                                         @RequestParam(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
            return ResponseEntity.ok(obrazacIOService.raiseStatus(id, email, kvartal));
    }
    @GetMapping
    public ResponseEntity<List<ObrazacResponse>> getZakList(@RequestParam(name = "status") Integer status,
                                        @RequestParam(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
            return ResponseEntity.ok(obrazacIOService.findValidObrazacToRaise(email, status, kvartal));
    }

    @GetMapping(value = "/storno")
    public ResponseEntity<List<ObrazacResponse>> getZakListZaStorno(@RequestParam(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
            return ResponseEntity.ok(obrazacIOService.findValidObrazacToStorno(email, kvartal));
    }

    @PutMapping(value = "/storno/{id}")
    public ResponseEntity<String> stornoZakList(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "kvartal") Integer kvartal,
                                                @RequestParam(name = "opis") String opis) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
            return ResponseEntity.ok(obrazacIOService.stornoObrIOFromUser(id, email, kvartal, opis));
    }
}
