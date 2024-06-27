package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public  class StatusService {

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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

        return "Status obrasca je podignut na nivo  " + savedEntity.getSTATUS() + "!";
    }

    //resolve if sttus is for odobravanje, overavanje or poslat DBK-u
    public <T extends StatusUpdatable> void resolveObrazacAccordingStatus(T entity, Integer status) throws Exception {

        var actualStatus = entity.getSTATUS();
        if (actualStatus >= 20) {
            throw new Exception("Dokument je vec poslat Vasem DBK-u!");
        } else if(actualStatus == 0 && status == 10) {
            throw new Exception("Dokument jos nije overen, \nidite na opciju overavanje!");
        } else if(actualStatus == 10 && status == 0) {
            throw new Exception("Dokument je vec overen, \nmozete ici na opciju odobravanje!");
        }
    }

    public <Actual extends StatusUpdatable, Next extends StatusUpdatable> void resolveObrazacAccordingNextObrazac(Actual actual, Next next) throws Exception {

        // TODO this block remove after implementing Obrazac5 in app
        if (actual instanceof ObrazacIO) {
            if (actual.getSTATUS() > next.getSTATUS() + 10) {
                throw new ObrazacException("Ne postoji obrazac kojem mozete podici status\n" +
                        "Morate prethodno podici status Obrascu 5 !");
            }
        }
        //TODO

        if (actual.getSTATUS() > next.getSTATUS()) {
            throw new ObrazacException("Ne postoji obrazac kojem mozete podici status\n" +
                    "Morate prethodno izjednaciti status narednog dokumenta !");
        }
    }

    public <Actual extends StatusUpdatable, Previous extends StatusUpdatable> void resolveObrazacAccordingPreviousObrazac(Actual actual, Previous previous) throws Exception {

        if (actual.getSTATUS() == previous.getSTATUS()) {
            throw new ObrazacException("Ne postoji obrazac kojem mozete podici status\n" +
                    "Morate prethodno podici status prethodnog dokumenta !");
        }
    }
    }
