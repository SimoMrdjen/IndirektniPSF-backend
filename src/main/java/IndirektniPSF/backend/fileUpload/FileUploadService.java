package IndirektniPSF.backend.fileUpload;

import IndirektniPSF.backend.parameters.AbParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileUploadService extends AbParameterService {

    @Value("${upload.path}") // Configure this in your application properties
    private String uploadPath;

    //private final ZakljucniListZbService zakljucniListZbService;
    //private final ParametersService parameterService;


    public String getDateAndTimeAsPartOfFilePath() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
        return LocalDateTime.now().format(formatter);
    }

    public String createPath(Integer year, String email, Integer kvartal, String typeOfObrazac ) {

        Integer jbbk =  this.getJbbksIBK(email);
        String path = uploadPath + "/" + year;
        File directory = new File(path);
        if (!directory.exists()) {
            boolean result =
                    directory.mkdir();
        }
        path += "/KVARTAL_" + kvartal;
        File directoryKvartal = new File(path);
        if (!directoryKvartal.exists()) {
            boolean result =
                    directoryKvartal.mkdir();
        }
        path += "/" + jbbk;
        File directoryJbbk = new File(path);
        if (!directoryJbbk.exists()) {
            boolean result =
                    directoryJbbk.mkdir();
        }
        return path;
    }

    public void saveExcelFile(Integer year, String email, Integer kvartal, String typeOfObrazac, MultipartFile file) {
        String uploadPath = this.createPath(year, email, kvartal, typeOfObrazac);

        try {
            String filePath = uploadPath + File.separator + this.getDateAndTimeAsPartOfFilePath() + " " +
                    file.getOriginalFilename();
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveTxtFile(Integer year, String email, Integer kvartal, String typeOfObrazac, String content) {

        String uploadPath = this.createPath(year, email, kvartal, typeOfObrazac);
        var fileName = this.getDateAndTimeAsPartOfFilePath() + ".txt";
        Path filePath = Paths.get(uploadPath, fileName);
        if(content.isEmpty()) {
            content = "Obrazac je uspesno ucitan";
        }

        try {
            FileWriter fileWriter = new FileWriter(filePath.toString());
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer findYear(Integer kvartal) {
        var year = LocalDateTime.now().getYear();
        return (kvartal == 4 || kvartal == 5) ? (year - 1) : year;
    }

    public void saveTxtAndExcelFile(String email, Integer kvartal, String typeOfObrazac,
                                    MultipartFile file, String content, String exceptionMessage) {
        var year = this.findYear(kvartal);
        String finalContent = exceptionMessage == null ? content : exceptionMessage;
        this.saveTxtFile(year, email, kvartal, typeOfObrazac, finalContent);
        this.saveExcelFile(year, email, kvartal, typeOfObrazac, file);
    }

//    public void saveTxtAndExcelFile( String email, Integer kvartal, String typeOfObrazac,
//                                    MultipartFile file, String content) {
//
//        var year = this.findYear(kvartal);
//        this.saveTxtFile(year, email, kvartal, typeOfObrazac, content);
//        this.saveExcelFile(year, email, kvartal,typeOfObrazac, file);
//    }
}
