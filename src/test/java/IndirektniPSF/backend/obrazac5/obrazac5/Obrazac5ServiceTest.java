package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class Obrazac5ServiceTest {

    @InjectMocks
    Obrazac5Service service;
    @Mock
    ArhbudzetService arhbudzetService;
    Integer kvartal = 1;
    Integer jbbk = 123;
    String oznakaGlave = "GL123";
    Integer sifSekret = 456;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public  void shouldThrowIfCheckPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet() {
        Double prihodiFromPokrajinaFromExcel = 500.0;
        Double date = (double)service.getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet = 400.0; // Mismatched value to trigger exception

        when(arhbudzetService.sumUplataIzBudzetaForIndKor(sifSekret, date, oznakaGlave, jbbk))
                .thenReturn(prihodFromArhBudzet);

        Assertions.assertThrows(ObrazacException.class, () ->
                service.checkPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet(
                        prihodiFromPokrajinaFromExcel, kvartal, jbbk, oznakaGlave, sifSekret
                )
        );
    }

    @Test
    public  void shouldNotThrowIfCheckPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet() {
        Double prihodiFromPokrajinaFromExcel = 500.0;

        Double date = (double)service.getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet = 500.0; // Matching value to avoid exception

        when(arhbudzetService.sumUplataIzBudzetaForIndKor(sifSekret, date, oznakaGlave, jbbk))
                .thenReturn(prihodFromArhBudzet);

        Assertions.assertDoesNotThrow(() ->
                service.checkPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet(
                        prihodiFromPokrajinaFromExcel, kvartal, jbbk, oznakaGlave, sifSekret
                )
        );
    }

    @Test
    void shouldThrowForKonto791100() {
        Double konto791100FromExcel = 500.0;
        Double date = (double)service.getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet = 400.0;

        when(arhbudzetService.sumUplataIzBudzetaForIndKorForIzvoriFin(sifSekret, date, jbbk))
                .thenReturn(prihodFromArhBudzet);

        assertThrows(ObrazacException.class, () ->
                service.checkKonto791100InObrazacAgainstDataInArhBudzet(
                        konto791100FromExcel, kvartal, jbbk, sifSekret
                )
        );
    }

    @Test
    void shouldNotThrowForKonto791100() {
        Double konto791100FromExcel = 500.0;
        Double date = (double)service.getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet = 500.0;

        when(arhbudzetService.sumUplataIzBudzetaForIndKorForIzvoriFin(sifSekret, date, jbbk))
                .thenReturn(prihodFromArhBudzet);

        assertDoesNotThrow(() ->
                service.checkKonto791100InObrazacAgainstDataInArhBudzet(
                        konto791100FromExcel, kvartal, jbbk, sifSekret
                )
        );
    }
}