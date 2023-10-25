package IndirektniPSF.backend.download;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DownloadService {

    public Resource download(String typeOfObrazac) {

        try {
            Resource resource = new FileSystemResource(getPath(typeOfObrazac));
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Error: File not found or not readable!");
            }

            return  resource;

        } catch (Exception ex) {
            throw new RuntimeException("Error while downloading the file. Error was: " + ex.getMessage());
        }
    }

    private Path getPath(String typeOfObrazac) {

        var fileName =typeOfObrazac + ".xlsx";
        return Paths.get("C:/Users/Simo/Desktop/Obrasci", fileName).normalize();
        //PRODUCTION
//            Path filePath = Paths.get("C:/Users/pavel/Desktop/Obrasci", fileName).normalize();
    }
}
