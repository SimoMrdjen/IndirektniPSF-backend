package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class IfObrazacCheckerTest {

    @InjectMocks
    ObrazacIOService checker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void checkIfKvartalIsForValidPeriod() {

        Integer year = LocalDate.now().getYear();

        var date1 =  LocalDate.of(year, 4, 10);
        var date2 =  LocalDate.of(year, 7, 10);
        var date3 = LocalDate.of(year, 10, 10);
        var date4 =  LocalDate.of(year + 1, 01, 10);
        var date5 =  LocalDate.of(year + 1, 04, 10);
        var date1False =  LocalDate.of(year, 3, 31);
        var date2False =  LocalDate.of(year, 6, 30);
        var date3False = LocalDate.of(year, 9, 30);
        var date4False =  LocalDate.of(year , 12, 31);

        assertThrows(IllegalArgumentException.class, () -> checker.checkIfKvartalIsForValidPeriod(1,2023, date1False));
        assertDoesNotThrow(() -> checker.checkIfKvartalIsForValidPeriod(1, 2023,date1));
        assertThrows(IllegalArgumentException.class, () -> checker.checkIfKvartalIsForValidPeriod(2,2023, date2False));
        assertDoesNotThrow(() -> checker.checkIfKvartalIsForValidPeriod(2, 2023,date2));
        assertThrows(IllegalArgumentException.class, () -> checker.checkIfKvartalIsForValidPeriod(3,2023, date3False));
        assertDoesNotThrow(() -> checker.checkIfKvartalIsForValidPeriod(3, 2023,date3));
        assertThrows(IllegalArgumentException.class, () -> checker.checkIfKvartalIsForValidPeriod(4,2023, date4False));
        assertDoesNotThrow(() -> checker.checkIfKvartalIsForValidPeriod(4, 2023,date4));
        assertDoesNotThrow(() -> checker.checkIfKvartalIsForValidPeriod(5, 2023,date5));




    }

    @Test
    void getLastDayOfKvartal() {

        Integer year = LocalDate.now().getYear();
        var date1 =  LocalDate.of(year, 3, 31);
        var date2 =  LocalDate.of(year, 6, 30);
        var date3 = LocalDate.of(year, 9, 30);
        var date4 =  LocalDate.of(year - 1, 12, 31);

        assertEquals(date1, checker.getLastDayOfKvartal(1));
        assertEquals(date2, checker.getLastDayOfKvartal(2));
        assertEquals(date3, checker.getLastDayOfKvartal(3));
        assertEquals(date4, checker.getLastDayOfKvartal(4));
        assertEquals(date4, checker.getLastDayOfKvartal(5));
    }

    @Test
    void checkIfExistValidObrazacYet() {
    }
}