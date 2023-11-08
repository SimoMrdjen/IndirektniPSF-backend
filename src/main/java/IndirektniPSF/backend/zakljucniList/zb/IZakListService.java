package IndirektniPSF.backend.zakljucniList.zb;


import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;

import java.util.List;

public interface IZakListService {

    void checkDuplicatesKonta(List<ZakljucniListDto> dtos) throws Exception;
}
