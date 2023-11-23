package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.review.ObrazacResponse;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

public interface IfObrazacService <T>{

    StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email)throws Exception;
   T findObrazacById(Integer id, Integer kvartal)throws Exception;
    ObrazacResponse getObrazactWithDetailsForResponseById(Integer id, Integer kvartal) throws Exception;

}
