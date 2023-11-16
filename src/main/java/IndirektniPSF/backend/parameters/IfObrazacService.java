package IndirektniPSF.backend.parameters;

import org.springframework.web.multipart.MultipartFile;

public interface IfObrazacService {

    StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email)throws Exception;

}
