package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.excel.ExcelService;
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
@RequestMapping(value = "/api/zakljucni_list")
@RequiredArgsConstructor
public class ZakljucniListZbController {

    private final ZakljucniListZbService zakljucniService;
    private final FileUploadService fileUploadService;
    private String message;

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<?> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            message = String.valueOf(zakljucniService.saveZakljucniFromExcel(file, kvartal, email));
            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"ZakljucniList", file, message);
        }
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<?> raiseStatus(@PathVariable(name = "id") Integer id,
                                         @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            String result = String.valueOf(zakljucniService.raiseStatus(id, email, kvartal));
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
            List<ObrazacResponse> result =  List.of(zakljucniService.findValidObrazacToRaise(email, status));
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
            List<ObrazacResponse> result =  List.of(zakljucniService.findValidObrazacToStorno(email, kvartal));
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            // Handle the exception and return an error response with status code 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/storno/{id}")
    public ResponseEntity<?> stornoZakList(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            String result = String.valueOf(zakljucniService.stornoZakList(id, email, kvartal));
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




}
