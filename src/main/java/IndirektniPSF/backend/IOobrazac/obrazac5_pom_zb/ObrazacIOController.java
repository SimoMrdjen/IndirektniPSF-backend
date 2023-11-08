package IndirektniPSF.backend.IOobrazac.obrazac5_pom_zb;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.fileUpload.FileUploadService;
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
    private String message;

    @PostMapping(value = "/{kvartal}")
    public ResponseEntity<?> addZakljucniFromExcel(@RequestBody MultipartFile file,
                                                   @PathVariable(name = "kvartal") Integer kvartal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            message = String.valueOf(obrazacIOService.saveZakljucniFromExcel(file, kvartal, email));
            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        finally{
            fileUploadService.saveTxtAndExcelFile(email, kvartal,"Obrazac_IO", file, message);
        }
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
