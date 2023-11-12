package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;

import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/obrazac_io")
@RequiredArgsConstructor
public class ObrazacIOController {

    private final ObrazacIOService obrazacIOService;
    private final FileUploadService fileUploadService;
//    private String message;

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<String> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String message = null;

        try {
            message = String.valueOf(obrazacIOService.saveIOFromExcel(file, kvartal, email));
//            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            message = e.getMessage();
            throw e;
//            return ResponseEntity.badRequest().body(e.getMessage());
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"Obrazac_IO", file, message, null);
        }
        return ResponseEntity.ok(message);

    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<String> raiseStatus(@PathVariable(name = "id") Integer id,
                                         @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
//        try{
            String result = String.valueOf(obrazacIOService.raiseStatus(id, email, kvartal));
            return ResponseEntity.ok(result);
//        }
//        catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }
    @GetMapping
    public ResponseEntity<List<ObrazacResponse>> getZakList(@RequestParam(name = "status") Integer status,
                                        @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
//        try {
            List<ObrazacResponse> result =  List.of(obrazacIOService.findValidObrazacToRaise(email, status));
            return ResponseEntity.ok(result);
//        }
//        catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @GetMapping(value = "/storno")
    public ResponseEntity<List<ObrazacResponse>> getZakListZaStorno(@RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
//        try {
            List<ObrazacResponse> result =  List.of(obrazacIOService.findValidObrazacToStorno(email, kvartal));
            return ResponseEntity.ok(result);
//        }
//        catch (Exception e) {
//            // Handle the exception and return an error response with status code 400
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @PutMapping(value = "/storno/{id}/")
    public ResponseEntity<String> stornoZakList(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
//        try {
            String result = String.valueOf(obrazacIOService.stornoObrIOFromUser(id, email, kvartal));
            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

//    @PostMapping(value = "/{kvartal}/{year}")
//    public ResponseEntity<Obrazac5_pom_zb>  addObrazacIO(//@RequestHeader(value = "Authorization") String token,
//                                                       @RequestBody List<ObrazacIODTO> dtos,
//                                                       @PathVariable(name = "kvartal") Integer kvartal,
//                                                       @PathVariable(name = "year") Integer year) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        return ResponseEntity.ok(obrazacIOService.saveObrazacIO(dtos, kvartal, year, email));
//
//    }

}
