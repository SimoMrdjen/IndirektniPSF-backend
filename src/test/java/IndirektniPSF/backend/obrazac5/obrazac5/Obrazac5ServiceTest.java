package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailsRepository;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5Mapper;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5details;
import IndirektniPSF.backend.raspodela.Raspodela;
import IndirektniPSF.backend.raspodela.RaspodelaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @Mock
    private Obrazac5Mapper mapper;
    @Mock
    private Obrazac5Service serviceMock;
    ObrazacIODetails ioDetail3;
    ObrazacIODetails ioDetail2;
    ObrazacIODetails ioDetail1;

    Integer kvartal = 1;
    Integer jbbk = 123;
    String oznakaGlave = "GL123";
    Integer sifSekret = 456;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        ioDetail1 = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421111)
                .DUGUJE(110.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(50.0)
                .POKRAJINA(40.0)
                .OPSTINA(20.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

       ioDetail3 = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(60.0)
               .POTRAZUJE(0.0)

               .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

      ioDetail2 = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .DUGUJE(100.0)
              .POTRAZUJE(0.0)

              .SIN_KONTO(4212)
                .KONTO(421212)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
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

        when(arhbudzetService.sumUplataIzBudzetaForIndKorForObr5(sifSekret, date, oznakaGlave, jbbk))
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
                .POTRAZUJE(0.0)
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
                .POTRAZUJE(0.0)

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

        Obrazac5details result = service.findProperDifferenceAccordingSinKonto(differncies, 4222);

        assertEquals(422200, result.getKonto());
    }

    @Test
    void testFindProperDifferenceAccordingSinKonto_WhenKontoDoesNotExist() {
        Obrazac5details detail1 = Obrazac5details.builder().konto(421100).build();
        Obrazac5details detail2 = Obrazac5details.builder().konto(422200).build();

        List<Obrazac5details> differncies = List.of(detail1, detail2);

        Obrazac5details result = service.findProperDifferenceAccordingSinKonto(differncies, 4244);
        assertNull(result); // No matching konto should return null
    }

    @Test
    void testPopulateColumnPrihodiForKolona6() {

        Raspodela raspodela = Raspodela.builder().kolona(6).build();

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(150.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().republika(200.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(150.0, ioDetails.getREPUBLIKA());
        assertEquals(50.0, singleDifference.getRepublika());
    }

    @Test
    void testPopulateColumnPrihodiForKolona7() {

        Raspodela raspodela = Raspodela.builder().kolona(7).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().pokrajina(80.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(80.0, ioDetails.getPOKRAJINA());
        assertEquals(0.0, singleDifference.getPokrajina());
    }

    @Test
    void testPopulateColumnPrihodiForKolona8() {

        Raspodela raspodela = Raspodela.builder().kolona(8).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(60.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().opstina(100.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(60.0, ioDetails.getOPSTINA());
        assertEquals(40.0, singleDifference.getOpstina());
    }

    @Test
    void testPopulateColumnPrihodiForKolona10() {

        Raspodela raspodela = Raspodela.builder().kolona(10).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(50.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().donacije(40.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(40.0, ioDetails.getDONACIJE());
        assertEquals(0.0, singleDifference.getDonacije());
    }

    @Test
    void testPopulateColumnPrihodiForKolona9() {

        Raspodela raspodela = Raspodela.builder().kolona(9).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(30.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().ooso(50.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(30.0, ioDetails.getOOSO());
        assertEquals(20.0, singleDifference.getOoso());
    }

    @Test
    void testPopulateColumnPrihodiForKolona11() {

        Raspodela raspodela = Raspodela.builder().kolona(11).build();
        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("0700")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(15.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();
        Obrazac5details singleDifference = Obrazac5details.builder().ostali(15.0).build();

        service.populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioDetails, singleDifference);

        assertEquals(15.0, ioDetails.getOSTALI());
        assertEquals(0.0, singleDifference.getOstali());
    }

    @Test
    void testPopulateColumnPrihodiInIO_WhenMatchingIzvorFinAndKolonaExist() {

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("1001")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(150.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
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

        service.populateColumnPrihodiInIO(ioDetails, differncies, raspodelas);

        assertEquals(150.0, ioDetails.getREPUBLIKA());
        assertEquals(50.0, singleDifference.getRepublika());
    }

    @Test
    void testPopulateColumnPrihodiInIO_WhenNoMatchingIzvorFin() {

        ObrazacIODetails ioDetails = ObrazacIODetails.builder()
                .IZVORFIN("9999")
                .SIN_KONTO(4211)
                .KONTO(421112)
                .DUGUJE(100.0)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
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

        service.populateColumnPrihodiInIO(ioDetails, differncies, raspodelas);

        assertEquals(0.0, ioDetails.getREPUBLIKA());
        assertEquals(150.0, singleDifference.getRepublika());
    }

    @Test
    void testAllocateExpensesByIncomeSource() {

        ObrazacIODetails ioDetail1 = ObrazacIODetails.builder()
                .KONTO(421100)
                .SIN_KONTO(4211)
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

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
                .SIN_KONTO(4222)
                .DUGUJE(150.0)
                .POTRAZUJE(0.0)

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

        service.allocateExpensesByIncomeSource(ioDetailsEmptyPrihodiColumns, differnciesBetweenObrIOAndObr5);

        assertEquals(100.0, ioDetail1.getREPUBLIKA());
        assertEquals(150.0, ioDetail2.getPOKRAJINA());

        verify(raspodelaService, times(1)).findIzvorFinIfNotUnique();
         verify(obrazacIODetailsService, times(1)).saveAll(ioDetailsEmptyPrihodiColumns);
    }
    @Test
    void testGetIoDetailsEmptyPrihodiColumns_WithValidData() {

        ObrazacIODetails ioDetail1 = ObrazacIODetails.builder()
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(50.0)
                .POKRAJINA(30.0)
                .OPSTINA(20.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

        ObrazacIODetails ioDetail2 = ObrazacIODetails.builder()
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(60.0)
                .POKRAJINA(30.0)
                .OPSTINA(10.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

        ObrazacIODetails ioDetail3 = ObrazacIODetails.builder()
                .DUGUJE(90.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

        ObrazacIO validIO = ObrazacIO.builder()
                .stavke(List.of(ioDetail1, ioDetail2, ioDetail3))
                .build();

        List<ObrazacIODetails> result = service.getIoDetailsEmptyPrihodiColumns(validIO);

        assertEquals(1, result.size());
        assertTrue(result.contains(ioDetail3));
    }

    @Test
    void testGetIoDetailsEmptyPrihodiColumns_WithAllMatchingData() {

        ObrazacIODetails ioDetail1 = ObrazacIODetails.builder()
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(50.0)
                .POKRAJINA(30.0)
                .OPSTINA(20.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

        ObrazacIODetails ioDetail2 = ObrazacIODetails.builder()
                .DUGUJE(100.0)
                .POTRAZUJE(0.0)

                .REPUBLIKA(60.0)
                .POKRAJINA(30.0)
                .OPSTINA(10.0)
                .DONACIJE(0.0)
                .OOSO(0.0)
                .OSTALI(0.0)
                .build();

        ObrazacIO validIO = ObrazacIO.builder()
                .stavke(List.of(ioDetail1, ioDetail2))
                .build();

        List<ObrazacIODetails> result = service.getIoDetailsEmptyPrihodiColumns(validIO);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIoDetailsEmptyPrihodiColumns_WithEmptyIO() {

        ObrazacIO validIO = ObrazacIO.builder()
                .stavke(List.of())
                .build();

        List<ObrazacIODetails> result = service.getIoDetailsEmptyPrihodiColumns(validIO);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDifference_WhenDifferencesExist() {

        Obrazac5details obr5Detail1 = Obrazac5details.builder()
                .konto(421100)
                .republika(200.0)
                .pokrajina(150.0)
                .build();

        Obrazac5details obr5Detail2 = Obrazac5details.builder()
                .konto(422200)
                .republika(100.0)
                .pokrajina(80.0)
                .build();

        Obrazac5details obrIODetail1 = Obrazac5details.builder()
                .konto(421100)
                .republika(100.0)
                .pokrajina(50.0)
                .build();

        Obrazac5details obrIODetail2 = Obrazac5details.builder()
                .konto(422200)
                .republika(90.0)
                .pokrajina(60.0)
                .build();

        List<Obrazac5details> detailsObr5 = List.of(obr5Detail1, obr5Detail2);
        List<Obrazac5details> detailsFromObrIO = List.of(obrIODetail1, obrIODetail2);

        List<Obrazac5details> differences = service.getDifferenceBetweenPrihodiFromIoAgainstObr5(detailsObr5, detailsFromObrIO);

        assertEquals(2, differences.size());

        Obrazac5details diff1 = differences.get(0);
        assertEquals(421100, diff1.getKonto());
        assertEquals(100.0, diff1.getRepublika());
        assertEquals(100.0, diff1.getPokrajina());

        Obrazac5details diff2 = differences.get(1);
        assertEquals(422200, diff2.getKonto());
        assertEquals(10.0, diff2.getRepublika());
        assertEquals(20.0, diff2.getPokrajina());
    }

    @Test
    void testGetDifference_WhenNoDifferences() {

        Obrazac5details obr5Detail1 = Obrazac5details.builder()
                .konto(421100)
                .republika(100.0)
                .pokrajina(50.0)
                .build();

        Obrazac5details obrIODetail1 = Obrazac5details.builder()
                .konto(421100)
                .republika(100.0)
                .pokrajina(50.0)
                .build();

        List<Obrazac5details> detailsObr5 = List.of(obr5Detail1);
        List<Obrazac5details> detailsFromObrIO = List.of(obrIODetail1);
        List<Obrazac5details> differences = service.getDifferenceBetweenPrihodiFromIoAgainstObr5(detailsObr5, detailsFromObrIO);
        System.out.println(differences);
        assertTrue(differences.isEmpty());
    }

    @Test
    void testGetDifference_WhenNoMatchingKonto() {

        Obrazac5details obr5Detail1 = Obrazac5details.builder()
                .konto(421100)
                .republika(200.0)
                .pokrajina(150.0)
                .opstina(0.0)
                .ostali(0.0)
                .donacije(0.0)
                .ooso(0.0)
                .build();

        Obrazac5details obrIODetail1 = Obrazac5details.builder()
                .konto(423300)
                .republika(100.0)
                .pokrajina(50.0)
                .opstina(0.0)
                .ostali(0.0)
                .donacije(0.0)
                .ooso(0.0)
                .build();

        List<Obrazac5details> detailsObr5 = List.of(obr5Detail1);
        List<Obrazac5details> detailsFromObrIO = List.of(obrIODetail1);

        List<Obrazac5details> differences =
                service.getDifferenceBetweenPrihodiFromIoAgainstObr5(detailsObr5, detailsFromObrIO);

        assertEquals(1, differences.size());
        Obrazac5details diff1 = differences.get(0);
        assertEquals(421100, diff1.getKonto());
        assertEquals(200.0, diff1.getRepublika());
        assertEquals(150.0, diff1.getPokrajina());
    }


    @Test
    void testCompleteColumnInObrIODetailsUsingDataFromObr5() {

        List<ObrazacIODetails> ioDetailsEmptyPrihodiColumns = List.of(ioDetail1, ioDetail2, ioDetail3);
        ObrazacIO validIO = ObrazacIO.builder().stavke(ioDetailsEmptyPrihodiColumns).build();

        Obrazac5details detail1 = Obrazac5details.builder().konto(421100)
                .republika(70.0)
                .pokrajina(60.0)
                .opstina(40.0)
                .ostali(00.0)
                .build();

        Obrazac5details detail2 = Obrazac5details.builder().konto(421200)
                .republika(60.0)
                .pokrajina(40.0)
                .build();

        List<Obrazac5details> detailsObr5 = List.of(detail1, detail2);

        List<Raspodela> raspodelas = List.of(
                Raspodela.builder().izvorFin("0700").kolona(6).build(),
                Raspodela.builder().izvorFin("0700").kolona(7).build(),
                Raspodela.builder().izvorFin("0700").kolona(8).build()
        );
        Obrazac5details detail3 = Obrazac5details.builder().konto(421100)
                .republika(50.0)
                .pokrajina(40.0)
                .opstina(20.0)
                .ostali(00.0)
                .build();

        Obrazac5details detail4 = Obrazac5details.builder().konto(421200)
                .republika(00.0)
                .pokrajina(00.0)
                .build();
        List<Obrazac5details> detailsObrIO = List.of(detail3, detail4);

        doNothing().when(obrazacIODetailsService).saveAll(anyList());
        when(raspodelaService.findIzvorFinIfNotUnique()).thenReturn(raspodelas);

        when(mapper.mapIOtoObr5(validIO.getStavke())).thenReturn(detailsObrIO);

        service.completeColumnInObrIODetailsUsingDataFromObr5(detailsObr5, validIO);

        assertEquals(20.0, ioDetail3.getREPUBLIKA());
        assertEquals(20.0, ioDetail3.getPOKRAJINA());
        assertEquals(20.0, ioDetail3.getOPSTINA());
        assertEquals(60.0, ioDetail2.getREPUBLIKA());
        assertEquals(40.0, ioDetail2.getPOKRAJINA());
        assertEquals(0.0, ioDetail2.getOPSTINA());
    }

    @Test
    void testGetRaspodelasForParticularIzvorWithMatchingElements() {

        Raspodela raspodela1 = Raspodela.builder().izvorFin("0700").kolona(6).build();
        Raspodela raspodela2 = Raspodela.builder().izvorFin("0800").kolona(7).build();
        Raspodela raspodela3 = Raspodela.builder().izvorFin("0700").kolona(8).build();
        List<Raspodela> raspodelas = List.of(raspodela1, raspodela2, raspodela3);

        List<Raspodela> result = service.getRaspodelasForParticularIzvor("0700", raspodelas);

        assertEquals(2, result.size());
        assertTrue(result.contains(raspodela1));
        assertTrue(result.contains(raspodela3));
    }

    @Test
    void testGetRaspodelasForParticularIzvorWithNoMatchingElements() {

        Raspodela raspodela1 = Raspodela.builder().izvorFin("0800").kolona(6).build();
        Raspodela raspodela2 = Raspodela.builder().izvorFin("0900").kolona(7).build();
        List<Raspodela> raspodelas = List.of(raspodela1, raspodela2);

        List<Raspodela> result = service.getRaspodelasForParticularIzvor("0700", raspodelas);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRaspodelasForParticularIzvorWithEmptyList() {

        List<Raspodela> raspodelas = List.of();

        List<Raspodela> result =service.getRaspodelasForParticularIzvor("0700", raspodelas);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRaspodelasForParticularIzvorWithNullList() {
        List<Raspodela> raspodelas = null;

        try {
            service.getRaspodelasForParticularIzvor("0700", raspodelas);
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("Cannot invoke \"java.util.List.stream()\""));
        }
    }

    @Test
    void testTransformObrIoToMap() {
        ObrazacIO  validIO = new ObrazacIO();

        List<ObrazacIODetails> stavke = Arrays.asList(
                ObrazacIODetails.builder()
                        .SIN_KONTO(3001)
                        .DUGUJE(0.0)
                        .POTRAZUJE(50.0)
                        .build(),
                ObrazacIODetails.builder()
                        .SIN_KONTO(4001)
                        .DUGUJE(0.0)
                        .POTRAZUJE(50.0)
                        .build(),
                ObrazacIODetails.builder()
                        .SIN_KONTO(4001)
                        .DUGUJE(100.0)
                        .POTRAZUJE(0.0)
                        .build(),
                ObrazacIODetails.builder()
                        .SIN_KONTO(4002)
                        .DUGUJE(300.0)
                        .POTRAZUJE(100.0)
                        .build(),
                ObrazacIODetails.builder()
                        .SIN_KONTO(4001) // Duplicate SIN_KONTO for testing merge function
                        .DUGUJE(0.0)
                        .POTRAZUJE(25.0)
                        .build()
        );

        validIO.setStavke(stavke);
        Map<Integer, Double> result = service.transformObrIoToMap(validIO);

        assertNotNull(result, "Resulting map should not be null");
        assertEquals(2, result.size(), "Map should contain 2 unique keys");

        assertEquals(175.0, result.get(400100), "Key 100100 should have the correct summed value");
        assertEquals(400.0, result.get(400200), "Key 100200 should have the correct summed value");
    }

    @Test
    void testTransformObr5ToMap() {
        Obrazac5DTO detail1 = Obrazac5DTO.builder().izvrsenje(100.0).konto(421100).build();
        Obrazac5DTO detail2 = Obrazac5DTO.builder().izvrsenje(200.0).konto(422200).build();
        Obrazac5DTO detail3 = Obrazac5DTO.builder().izvrsenje(300.0).konto(423300).build();

        List<Obrazac5DTO> details = List.of(detail1, detail2, detail3);
        Map<Integer, Double> mapDeatails = Map.of(421100,100.0,422200,200.0, 423300,300.0);

        assertEquals(mapDeatails, service.transformObr5ToMap(details));
    }

    @Test
    void testCheckIfExistDiffernciesWithMatchingKeysAndValues() {
        Map<Integer, Double> mapObr5 = Map.of(
                100100, 500.0,
                100200, 300.0,
                100300, 400.0
        );

        Map<Integer, Double> mapObrIo = Map.of(
                100100, 500.0,
                100200, 300.0,
                100300, 400.0
        );

        assertDoesNotThrow(() -> service.checkIfExistDiffernciesBetweenTroskoviForSinKontosIOAndOBr5(mapObr5, mapObrIo));
    }

    @Test
    void testCheckIfExistDiffernciesWithMissingKeys() {
        Map<Integer, Double> mapObr5 = Map.of(
                100100, 500.0,
                100200, 300.0
        );

        Map<Integer, Double> mapObrIo = Map.of(
                100100, 500.0,
                100300, 400.0
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkIfExistDiffernciesBetweenTroskoviForSinKontosIOAndOBr5(mapObr5, mapObrIo));

        String expectedMessage = "Sin. konto [100300]\n postoji u Obrascu IO ali ne i u Obrascu 5!\n" +
                "Sin. konto [100200]\n postoji u Obrascu 5 ali ne i u Obrascu IO!";

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testCheckIfExistDiffernciesWithValueDifferences() {
        Map<Integer, Double> mapObr5 = Map.of(
                100100, 500.0,
                100200, 300.0
        );

        Map<Integer, Double> mapObrIo = Map.of(
                100100, 400.0,
                100200, 300.0 // Difference in value
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkIfExistDiffernciesBetweenTroskoviForSinKontosIOAndOBr5(mapObr5, mapObrIo));
        System.out.println(exception.getMessage());

        String expectedMessage = "Postoje razlike u sin.kontu: 100100: Obr5=500.00, ObrIo=400.00";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}

