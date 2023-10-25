package IndirektniPSF.backend.fileUpload;

import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

//    @Value("${upload.path}") // Configure this in your application properties
//    private String uploadPath;

    private final FileUploadService service;
    private  final ZakljucniListZbService zbService;

//    @PostMapping("/excel/ZakljucniList/{kvartal}/{typeOfObrazac}")
//    public ResponseEntity<?> uploadZakljucniListExcel(@RequestBody MultipartFile file,
//                                  @PathVariable(name = "kvartal") Integer kvartal,
//                                  @PathVariable(name = "typeOfObrazac") String typeOfObrazac
//    ) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//
//
//        try {
//            String result = String.valueOf( zbService.saveZakljucniFromExcel(file, kvartal););
//            return ResponseEntity.ok(result);
//        }
//        catch (Exception e) {
//            // Handle the exception and return an error response with status code 400
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//
//
//    }

//    @PostMapping("/excel/{year}/{kvartal}/{typeOfObrazac}")
//    public String uploadExcelFile(@RequestParam("file") MultipartFile file,
//                                  @PathVariable(name = "year") Integer year,
//                                  @PathVariable(name = "kvartal") Integer kvartal,
//                                  @PathVariable(name = "typeOfObrazac") String typeOfObrazac
//    ) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        String uploadPath = service.createPath(year, email, kvartal, typeOfObrazac);
//
//        try {
//            String filePath = uploadPath + File.separator + service.getDateAndTimeAsPartOfFilePath() + " " +
//                    file.getOriginalFilename();
//            file.transferTo(new File(filePath));
//            return "File uploaded successfully!";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "File upload failed.";
//        }
//    }

    @PostMapping("/txt/{year}/{kvartal}/{typeOfObrazac}")
    public void uploadTxtFile(@RequestBody TxtContent content,
                                @PathVariable(name = "year") Integer year,
                                @PathVariable(name = "kvartal") Integer kvartal,
                                @PathVariable(name = "typeOfObrazac") String typeOfObrazac
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String uploadPath = service.createPath(year, email, kvartal, typeOfObrazac);
        var fileName = service.getDateAndTimeAsPartOfFilePath();
        Path filePath = Paths.get(uploadPath, fileName);

        try {
            FileWriter fileWriter = new FileWriter(filePath.toString());
            fileWriter.write(content.getText());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
