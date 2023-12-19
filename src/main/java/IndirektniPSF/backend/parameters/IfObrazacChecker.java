package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.exceptions.ObrazacException;

import java.time.LocalDate;
import java.time.Month;

public interface IfObrazacChecker {

     default <T extends StatusUpdatable>void isObrazacSentToDBK(T zb) throws Exception {

        if (zb.getSTATUS() >= 20) {
            throw new Exception("Obrazac je vec poslat vasem DBK-u");
        }
    }

    default <T extends StatusUpdatable> void isObrazacStorniran(T zb) throws Exception {

         if (zb.getSTORNO() == 1) {
            throw new Exception("Obrazac je storniran,\nmorate ucitati novu verziju!");
        }
    }

    default <T extends StatusUpdatable> void checkStatusAndStorno(T t) throws Exception {

         this.isObrazacStorniran(t);
         this.isObrazacSentToDBK(t);
    }
    default void chekIfKvartalIsCorrect(Integer kvartal, Integer excelKvartal, Integer year) throws ObrazacException {

        LocalDate currentDate = LocalDate.now();
         if(kvartal != excelKvartal) {
            throw new ObrazacException("Odabrani kvartal i kvartal u dokumentu nisu identicni!");
        }
        this.checkIfKvartalIsForValidPeriod(kvartal, year, currentDate);
    }

    default void checkIfKvartalIsForValidPeriod(Integer kvartal, Integer year, LocalDate currentDate) {

//        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        String message = "Datum ili godina ne odgovaraju \nkvartalu koji ste izabrali!";

        if (kvartal == 1 &&
                !(currentMonth == Month.APRIL
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 20
                        && currentYear == year)) {
            throw new IllegalArgumentException(message);
        } else if (kvartal == 2 &&
                !(currentMonth == Month.JULY
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 20
                        && currentYear == year)) {
            throw new IllegalArgumentException(message);
        } else if (kvartal == 3 &&
                !(currentMonth == Month.OCTOBER
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 20
                        && currentYear == year)) {
            throw new IllegalArgumentException(message);
        } else if (kvartal == 4 &&
                !(currentMonth == Month.JANUARY
                        && currentDate.getDayOfMonth() >= 1
                        && currentDate.getDayOfMonth() <= 20
                        && (currentYear - 1) == year)) {
            throw new IllegalArgumentException(message);
        } else if (kvartal == 5) {
            if (!((currentMonth.getValue() >= Month.JANUARY.getValue()
                    && currentMonth.getValue() <= Month.MAY.getValue())
                    && (currentYear - 1) == year)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    default LocalDate getLastDayOfKvartal(Integer kvaratl) {

        Integer year = LocalDate.now().getYear();
        if (kvaratl == 1) return LocalDate.of(year, 3, 31);
        if (kvaratl == 2) return LocalDate.of(year, 6, 30);
        if (kvaratl == 3) return LocalDate.of(year, 9, 30);
        return LocalDate.of(year - 1, 12, 31);
    }

    default Double getLastDayOfKvartalAsDouble(Integer kvartal) {
        return (double)getLastDayOfKvartal(kvartal).toEpochDay() + 25569;

    }

    default <T extends StatusUpdatable> void checkIfExistValidObrazacYet(T t) throws ObrazacException {
        if (t.getRADNA() == 1 && t.getSTORNO() == 0 ) {
            throw new ObrazacException("Za tekući kvartal već postoji učitan \nvažeći brazac!\nUkoliko zelite da ucitate novu verziju " +
                    "\nprethodnu morate stornirati!");
        }
    }

    default boolean areEqual(double a, double b) {
        return Math.abs(a - b) < 0.001;
    }

}
