package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.webjars.NotFoundException;

import java.time.LocalDate;

public abstract class AbParameterService {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PPartnerService pPartnerService;



    protected Integer getJbbksIBK(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return pPartnerService.getJBBKS(user.getSifra_pp());
    }
    protected Integer getJbbksIBK( User user) {

        return pPartnerService.getJBBKS(user.getSifra_pp());
    }

    protected Integer getYear(Integer kvartal) {
        return (kvartal == 4 || kvartal == 5) ? LocalDate.now().getYear() - 1 : LocalDate.now().getYear();
    }

    protected User getUser(String email) {
      return  userRepository.findByEmail(email).orElseThrow(() ->  new NotFoundException("Korisnik ne postoji!"));
    }

    protected void checkJbbks(User user, Integer jbbksExcell) throws Exception {
        var jbbkDb =this.getJbbksIBK(user);

        if (!jbbkDb.equals(jbbksExcell)) {
            throw new Exception("Niste uneli (odabrali) vaš JBKJS!");
        }
    }


//    protected void checkIfKvartalIsForValidPeriod(Integer kvartal, Integer year) {
//
//        LocalDate currentDate = LocalDate.now();
//        Month currentMonth = currentDate.getMonth();
//        int currentYear = currentDate.getYear();
//
//        if (kvartal == 1 &&
//                !(currentMonth == Month.APRIL
//                        && currentDate.getDayOfMonth() >= 1
//                        && currentDate.getDayOfMonth() <= 20
//                        && currentYear == year)) {
//            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
//        } else if (kvartal == 2 &&
//                !(currentMonth == Month.JULY
//                        && currentDate.getDayOfMonth() >= 1
//                        && currentDate.getDayOfMonth() <= 20
//                        && currentYear == year)) {
//            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
//        } else if (kvartal == 3 &&
//                !(currentMonth == Month.OCTOBER
//                        && currentDate.getDayOfMonth() >= 1
//                        && currentDate.getDayOfMonth() <= 20
//                        && currentYear == year)) {
//            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
//        } else if (kvartal == 4 &&
//                !(currentMonth == Month.JANUARY
//                        && currentDate.getDayOfMonth() >= 1
//                        && currentDate.getDayOfMonth() <= 20
//                        && (currentYear - 1) == year)) {
//            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
//        } else if (kvartal == 5) {
//            if (!((currentMonth.getValue() >= Month.JANUARY.getValue()
//                    && currentMonth.getValue() <= Month.MAY.getValue())
//                    && (currentYear - 1) == year)) {
//                throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
//            }
//        }
//    }


}
