package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public   class StatusService {

    public <T extends StatusUpdatable> String raiseStatusDependentOfActuallStatus(
            T entity, User user, JpaRepository repository) {

        var status = entity.getSTATUS();
        if (status == 0) {
            entity.setPODIGAO_STATUS(user.getSifraradnika());
        } else {
            entity.setPOSLAO_NAM(user.getSifraradnika());
        }
        entity.setSTATUS(status + 10);
        T savedEntity = (T) repository.save(entity);

        return "Entity status is raised to level " + savedEntity.getSTATUS() + "!";
    }

    public <T extends StatusUpdatable> void resolveObrazacAccordingStatus(T entity, Integer status) throws Exception {

        var actualStatus = entity.getSTATUS();
        if (actualStatus >= 20) {
            throw new Exception("Dokument je vec poslat Vasem DBK-u!");
        } else if(actualStatus == 0 && status == 10) {
            throw new Exception("Dokument jos nije odobren, \nidite na opciju odobravanje!");
        } else if(actualStatus == 10 && status == 0) {
            throw new Exception("Dokument je vec odobren, \nmozete ici na opciju overavanje!");
        }
    }


}
