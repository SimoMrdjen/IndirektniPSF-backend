package IndirektniPSF.backend.obrazac5.obrazacZb;

import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import IndirektniPSF.backend.security.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/obrazac_zb")
@RequiredArgsConstructor
public class ObrazacZbController {

    private final ObrazacZbService obrazacZbService;
    private final FileUploadService fileUploadService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<String> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
        String message = null;

        try {
            message = String.valueOf(obrazacZbService.saveZakljucniFromExcel(file, kvartal, email));
        }
        catch (Exception e) {
            message = e.getMessage();
            throw e;
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"Obrazac_5", file, message, null);
        }
        return ResponseEntity.ok(message);
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<String> raiseStatus(@PathVariable(name = "id") Integer id,
                                         @RequestParam(name = "kvartal") Integer kvartal) throws Exception {

        String email = authenticationService.getAuthenticatedUserEmail();
            String result = String.valueOf(obrazacZbService.raiseStatus(id, email));
            return ResponseEntity.ok(result);
    }
    @GetMapping
    public ResponseEntity<List<ObrazacResponse>> getZakList(@RequestParam(name = "status") Integer status,
                                        @RequestParam(name = "kvartal") Integer kvartal) {

        String email = authenticationService.getAuthenticatedUserEmail();
            List<ObrazacResponse> result =  List.of(obrazacZbService.findValidObrazacToRaise(email, status, kvartal));
            return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/storno")
    public ResponseEntity<List<ObrazacResponse>> getZakListZaStorno(@RequestParam(name = "kvartal") Integer kvartal) {

        String email = authenticationService.getAuthenticatedUserEmail();
            List<ObrazacResponse> result =  List.of(obrazacZbService.findValidObrazacToStorno(email, kvartal));
            return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/storno/{id}/")
    public ResponseEntity<String> stornoZakList(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "kvartal") Integer kvartal) {

        String email = authenticationService.getAuthenticatedUserEmail();
            String result = String.valueOf(obrazacZbService.stornoObr5FromUser(id, email, kvartal));
            return ResponseEntity.ok(result);
    }
}
