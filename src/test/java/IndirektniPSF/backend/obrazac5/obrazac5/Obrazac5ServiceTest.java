package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailsRepository;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5details;
import IndirektniPSF.backend.raspodela.Raspodela;
import IndirektniPSF.backend.raspodela.RaspodelaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class Obrazac5ServiceTest {

    @InjectMocks
    Obrazac5Service service;
    @Mock
    ArhbudzetService arhbudzetService;
    @Mock
    RaspodelaService raspodelaService;
    @Mock
    ObrazacIODetailsRepository obrazacIODetailsRepository;
    @Mock
    ObrazacIODetailService obrazacIODetailsService;

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

    @Test
    void testSumOfIzvori() {

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .POKRAJINA(100.0)
                .REPUBLIKA(200.0)
                .OPSTINA(150.0)
                .OOSO(50.0)
                .DONACIJE(30.0)
                .OSTALI(20.0)
                .build();

        double sum = ioDetails.getPOKRAJINA() + ioDetails.getREPUBLIKA() +
                ioDetails.getOPSTINA() + ioDetails.getOOSO() +
                ioDetails.getDONACIJE() + ioDetails.getOSTALI();
        assertEquals(550.0, sum);
    }

    @Test
    void testIsNotEqualDugujeAndSumOfIzvori_WhenNotEqual() {

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .DUGUJE(500.0)
                .POKRAJINA(100.0)
                .REPUBLIKA(150.0)
                .OPSTINA(100.0)
                .OOSO(50.0)
                .DONACIJE(70.0)
                .OSTALI(20.0)
                .build();

        boolean result = service.isNotEqualDugujeAndSumOfIzvori(ioDetails);
        assertTrue(result); //  500 != (100+150+100+50+70+20)
    }

    @Test
    void testIsNotEqualDugujeAndSumOfIzvori_WhenEqual() {

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .DUGUJE(490.0)
                .POKRAJINA(100.0)
                .REPUBLIKA(150.0)
                .OPSTINA(100.0)
                .OOSO(50.0)
                .DONACIJE(70.0)
                .OSTALI(20.0)
                .build();
        boolean result = service.isNotEqualDugujeAndSumOfIzvori(ioDetails);
        assertFalse(result); //  490 == (100+150+100+50+70+20)
    }

    @Test
    void testFindProperDifferenceAccordingSinKonto_WhenKontoExists() {

        Obrazac5details detail1 = Obrazac5details.builder().konto(421100).build();
        Obrazac5details detail2 = Obrazac5details.builder().konto(422200).build();
        Obrazac5details detail3 = Obrazac5details.builder().konto(423300).build();

        List<Obrazac5details> differncies = List.of(detail1, detail2, detail3);

        Obrazac5details result = service.findProperDifferenceAccordingSinKonto(differncies, 422200);

        assertEquals(422200, result.getKonto());
    }

    @Test
    void testFindProperDifferenceAccordingSinKonto_WhenKontoDoesNotExist() {
        Obrazac5details detail1 = Obrazac5details.builder().konto(421100).build();
        Obrazac5details detail2 = Obrazac5details.builder().konto(422200).build();

        List<Obrazac5details> differncies = List.of(detail1, detail2);

        Obrazac5details result = service.findProperDifferenceAccordingSinKonto(differncies, 424400);
        assertNull(result); // No matching konto should return null
    }

    @Test
    void testPopulateColumnPrihodiForKolona6() {

        Raspodela raspodela = Raspodela.builder().kolona(6).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(150.0).REPUBLIKA(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().republika(200.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(150.0, ioDetails.getREPUBLIKA());
        assertEquals(50.0, singleDifference.getRepublika());
    }

    @Test
    void testPopulateColumnPrihodiForKolona7() {

        Raspodela raspodela = Raspodela.builder().kolona(7).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(100.0).POKRAJINA(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().pokrajina(80.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(80.0, ioDetails.getPOKRAJINA());
        assertEquals(0.0, singleDifference.getPokrajina());
    }

    @Test
    void testPopulateColumnPrihodiForKolona8() {

        Raspodela raspodela = Raspodela.builder().kolona(8).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(60.0).OPSTINA(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().opstina(100.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(60.0, ioDetails.getOPSTINA());
        assertEquals(40.0, singleDifference.getOpstina());
    }

    @Test
    void testPopulateColumnPrihodiForKolona10() {

        Raspodela raspodela = Raspodela.builder().kolona(10).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(50.0).DONACIJE(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().donacije(40.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(40.0, ioDetails.getDONACIJE());
        assertEquals(0.0, singleDifference.getDonacije());
    }

    @Test
    void testPopulateColumnPrihodiForKolona9() {

        Raspodela raspodela = Raspodela.builder().kolona(9).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(30.0).OOSO(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().ooso(50.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(30.0, ioDetails.getOOSO());
        assertEquals(20.0, singleDifference.getOoso());
    }

    @Test
    void testPopulateColumnPrihodiForKolona11() {

        Raspodela raspodela = Raspodela.builder().kolona(11).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder().DUGUJE(15.0).OSTALI(0.0).build();
        Obrazac5details singleDifference = Obrazac5details.builder().ostali(15.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(15.0, ioDetails.getOSTALI());
        assertEquals(0.0, singleDifference.getOstali());
    }

    @Test
    void testPopulateColumnPrihodiInIO_WhenMatchingIzvorFinAndKolonaExist() {
        // Arrange
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .KONTO(421100)
                .DUGUJE(150.0)
                .IZVORFIN("1001")
                .REPUBLIKA(0.0)
                .build();

        Obrazac5details singleDifference = Obrazac5details.builder()
                .konto(421100)
                .republika(200.0)
                .build();

        Raspodela raspodela1 = Raspodela.builder()
                .izvorFin("1001")
                .kolona(6) // REPUBLIKA
                .build();

        List<Obrazac5details> differncies = List.of(singleDifference);
        List<Raspodela> raspodelas = List.of(raspodela1);

        // Act
        service.populateColumnPrihodiInIO(ioDetails, differncies, raspodelas);

        // Assert
        assertEquals(150.0, ioDetails.getREPUBLIKA()); // Updated column
        assertEquals(50.0, singleDifference.getRepublika()); // Remaining difference
    }

    @Test
    void testPopulateColumnPrihodiInIO_WhenNoMatchingIzvorFin() {
        // Arrange
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .KONTO(421100)
                .DUGUJE(100.0)
                .REPUBLIKA(0.0)
                .IZVORFIN("9999")
                .build();

        Obrazac5details singleDifference = Obrazac5details.builder()
                .konto(421100)
                .republika(150.0)
                .build();

        Raspodela raspodela1 = Raspodela.builder()
                .izvorFin("1001")
                .kolona(6) // REPUBLIKA
                .build();

        List<Obrazac5details> differncies = List.of(singleDifference);
        List<Raspodela> raspodelas = List.of(raspodela1);

        // Act
        service.populateColumnPrihodiInIO(ioDetails, differncies, raspodelas);

        // Assert
        assertEquals(0.0, ioDetails.getREPUBLIKA()); // No change as izvorFin does not match
        assertEquals(150.0, singleDifference.getRepublika()); // No difference used
    }

    @Test
    void testAllocateExpensesByIncomeSource() {
        // Arrange
        ObrazacIODetails ioDetail1 = ObrazacIODetails.builder()
                .KONTO(421100)
                .DUGUJE(100.0)
                .IZVORFIN("1001")
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OSTALI(0.0)
                .OOSO(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .build();

        ObrazacIODetails ioDetail2 = ObrazacIODetails.builder()
                .KONTO(422200)
                .DUGUJE(150.0)
                .IZVORFIN("2002")
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OSTALI(0.0)
                .OOSO(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .build();

        Obrazac5details diff1 = Obrazac5details.builder()
                .konto(421100)
                .republika(200.0)
                .build();

        Obrazac5details diff2 = Obrazac5details.builder()
                .konto(422200)
                .pokrajina(150.0)
                .build();

        Raspodela raspodela1 = Raspodela.builder()
                .izvorFin("1001")
                .kolona(6) // REPUBLIKA
                .build();

        Raspodela raspodela2 = Raspodela.builder()
                .izvorFin("2002")
                .kolona(7) // POKRAJINA
                .build();

        List<ObrazacIODetails> ioDetailsEmptyPrihodiColumns = List.of(ioDetail1, ioDetail2);
        List<Obrazac5details> differnciesBetweenObrIOAndObr5 = List.of(diff1, diff2);
        List<Raspodela> raspodelas = List.of(raspodela1, raspodela2);

        when(raspodelaService.findIzvorFinIfNotUnique()).thenReturn(raspodelas);
        doNothing().when(obrazacIODetailsService).saveAll(ioDetailsEmptyPrihodiColumns);

        // Act
        service.allocateExpensesByIncomeSource(ioDetailsEmptyPrihodiColumns, differnciesBetweenObrIOAndObr5);

        // Assert
        assertEquals(100.0, ioDetail1.getREPUBLIKA()); // Check allocation for REPUBLIKA
        assertEquals(150.0, ioDetail2.getPOKRAJINA()); // Check allocation for POKRAJINA

        verify(raspodelaService, times(1)).findIzvorFinIfNotUnique();
         verify(obrazacIODetailsService, times(1)).saveAll(ioDetailsEmptyPrihodiColumns);
    }
    }