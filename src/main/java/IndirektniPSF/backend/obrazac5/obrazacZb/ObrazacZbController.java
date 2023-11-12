package IndirektniPSF.backend.obrazac5.obrazacZb;

import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.parameters.ObrazacResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/obrazac_zb")
@RequiredArgsConstructor
public class ObrazacZbController {

    private final ObrazacZbService obrazacZbService;
    private final FileUploadService fileUploadService;
    private String message;

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<?> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            message = String.valueOf(obrazacZbService.saveZakljucniFromExcel(file, kvartal, email));
            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"Obrazac_5", file, message);
        }
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<?> raiseStatus(@PathVariable(name = "id") Integer id,
                                         @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            String result = String.valueOf(obrazacZbService.raiseStatus(id, email));
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getZakList(@RequestParam(name = "status") Integer status,
                                        @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            List<ObrazacResponse> result =  List.of(obrazacZbService.findValidObrazacToRaise(email, status, kvartal));
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/storno")
    public ResponseEntity<?> getZakListZaStorno(@RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            List<ObrazacResponse> result =  List.of(obrazacZbService.findValidObrazacToStorno(email, kvartal));
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            // Handle the exception and return an error response with status code 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/storno/{id}/")
    public ResponseEntity<?> stornoZakList(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            String result = String.valueOf(obrazacZbService.stornoObr5FromUser(id, email, kvartal));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


//    @PostMapping(value = "/{kvartal}")
//    public ResponseEntity<?> addObrazacZb(@RequestBody List<Obrazac5DTO> dtos,
//                                                  @PathVariable(name = "kvartal") Integer kvartal) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        try {
//            return ResponseEntity.ok(obrazacZbService.saveObrazac5(dtos, kvartal, email));
//        }
//        catch (Exception e) {
//            // Handle the exception and return an error response with status code 400
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}
