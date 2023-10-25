package IndirektniPSF.backend.download;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/download")
@RequiredArgsConstructor
public class DownloadObrazacaController {

    private final DownloadService service;

    @GetMapping("/")
            //("/{typeOfObrazac}")
    public ResponseEntity<?> downloadFile(@RequestParam(name = "typeOfObrazac") String typeOfObrazac) {

        Resource resource = service.download(typeOfObrazac);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
