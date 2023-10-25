package IndirektniPSF.backend.parameters;

import java.time.LocalDate;
import java.time.Month;

public interface ParametersService {

    default void checkIfKvartalIsForValidPeriod(Integer kvartal, Integer year) {

        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();

        if (kvartal == 1  &&
                !(currentMonth == Month.APRIL
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 15
                        && currentYear == year)) {
           throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
        } else if (kvartal == 2  &&
                !(currentMonth == Month.JULY
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 15
                        && currentYear == year)) {
            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
        } else if (kvartal == 3  &&
                !(currentMonth == Month.OCTOBER
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 15
                        && currentYear == year)) {
            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
        } else if(kvartal == 1  &&
                !(currentMonth == Month.JANUARY
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 15
                        && (currentYear -1) == year)){
            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
        } else if(kvartal == 5  &&
                !(currentMonth == Month.JANUARY
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 15
                        && (currentYear - 1) == year)) {
            throw new IllegalArgumentException("Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!");
        }
        }
    }



