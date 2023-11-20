package IndirektniPSF.backend.parameters;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

public interface IfObrazacService <T>{

    StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email)throws Exception;
   T findObrazacById(Integer id)throws Exception;

}
