package IndirektniPSF.backend.obrazac5.ppartner;

import IndirektniPSF.backend.security.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Component
public class PPartnerService {
    private final PPartnerRepository pPartnerRepository;
    private final UserService userService;

    public Integer getJBBKS(Integer ppartner) {

        return
                pPartnerRepository.findById(ppartner)
                .get()
                .getJbkbs();
    }

    public  String getPartner(Integer pparner) {
        return pPartnerRepository.findById(pparner)
                .get()
                .getPartner();
    }
}
