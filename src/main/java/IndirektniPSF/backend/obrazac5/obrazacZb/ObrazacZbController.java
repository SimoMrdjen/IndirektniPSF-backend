package IndirektniPSF.backend.obrazac5.obrazacZb;

import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
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

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<?> addObrazacZb(@RequestBody List<Obrazac5DTO> dtos,
                                                  @PathVariable(name = "kvartal") Integer kvartal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            return ResponseEntity.ok(obrazacZbService.saveObrazac5(dtos, kvartal, email));
        }
        catch (Exception e) {
            // Handle the exception and return an error response with status code 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<?> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var year = fileUploadService.findYear(kvartal);
        String typeOfObrazac = "Obrazac_5";
        fileUploadService.saveExcelFile(year, email, kvartal,typeOfObrazac, file);

        try {
            String result = String.valueOf(obrazacZbService.saveZakljucniFromExcel(file, kvartal, email));
            fileUploadService.saveTxtFile(year, email, kvartal, typeOfObrazac, result);
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            fileUploadService.saveTxtFile(year, email, kvartal, typeOfObrazac, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //TODO -add storno put method provide id of obrazac call storno(id, email)
}
