package IndirektniPSF.backend.IOobrazac.obrazacIO;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.PomObrazac;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIOMapper;
import IndirektniPSF.backend.arhbudzet.Arhbudzet;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.izvor.Izvor;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5Service;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.parameters.StatusService;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListDetails;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ObrazacIOServiceTest {

    @InjectMocks ObrazacIOService service;
    @Mock ObrazacIORepository obrazacIOrepository;
    @Mock SekretarijarService sekretarijarService;
    @Mock PPartnerService pPartnerService;
    @Mock ObrazacIODetailService obrazacIODetailService;
    @Mock UserRepository userRepository;
    @Mock  Obrazac5Service obrazac5Service;
//    @Mock StringBuilder responseMessage = new StringBuilder();
    @Mock ExcelService excelService;
    @Mock ObrazacIOMapper mapper;
    @Mock  ZakljucniListZbRepository zakljucniRepository;
    @Mock  StatusService statusService;
    @Mock GlavaSviService glavaSviService;
    @Mock ArhbudzetService arhbudzetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldThrowsWhenCheckForDuplicatesStandKlasif()  {
        List<ObrazacIODTO> obrazacList = List.of(
                new ObrazacIODTO(1, "A", 100, "X", "X",10.0, 5.0),
                new ObrazacIODTO(1, "A", 100, "X", "X",10.0, 3.0),
                new ObrazacIODTO(2, "A", 100, "X", "X",10.0, 3.0)
        );

        var listSize =obrazacList.size();
        var setSize = obrazacList.stream().collect(Collectors.toSet()).size();
        var listSize2 =obrazacList.size();

        System.out.println("setSize = " + setSize);
        System.out.println("listSize = " + listSize);
        System.out.println("listSize2 = " + listSize2);


        assertThrows(ObrazacException.class,() -> service.checkForDuplicatesStandKlasif(obrazacList));
    }

    @Test
    void testcheckIfStandKlasifBetweenExcenAndFinPlan() {

        ObrazacIODTO obr1 = new ObrazacIODTO(1, "A", 1001, "X", null, null, null);
        ObrazacIODTO obr2 = new ObrazacIODTO(1, "A", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr3 = new ObrazacIODTO(1, "A", 1003, "X", "X",10.0, 3.0);
        ObrazacIODTO obr4 = new ObrazacIODTO(1, "A", 1001, "X", "X",10.0, 5.0);
        ObrazacIODTO obr5 = new ObrazacIODTO(1, "b", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr6 = new ObrazacIODTO(2, "A", 1001, "X", "X",10.0, 3.0);

        System.out.println(service.checkIfStandKlasifBetweenExcenAndFinPlan(List.of(obr6, obr1),
                List.of(obr1, obr2, obr3) ));

        System.out.println(service.checkIfStandKlasifBetweenExcenAndFinPlan(List.of(obr6, obr1),
                List.of(obr1, obr6)));

        assertEquals(obr6.toString(),service.checkIfStandKlasifBetweenExcenAndFinPlan(List.of(obr6, obr1),
                List.of(obr1, obr2, obr3) ));

        assertEquals("",service.checkIfStandKlasifBetweenExcenAndFinPlan(List.of(obr6, obr1),
                List.of(obr1, obr6)));

        assertEquals(obr5.toString(),service.checkIfStandKlasifBetweenExcenAndFinPlan(List.of(obr5, obr1),
                List.of(obr1, obr3, obr6)));
    }

//    @Test
//   void testcheckIfStandKlasifFromExcelExistInFinPlana() throws ObrazacException {
//        Arhbudzet arhbudzet = new Arhbudzet(1, "Nalog001", 100L, 5000.0, 30, 4001, 20200101.0,
//                "Glava01", 3578,
//                new Izvor("01","budz"), 10, 200, 1);
//        ObrazacIODTO dtoFromArh =  ObrazacIODTO.builder()
//                .izvorFin("01")
//                .funkKlas("200")
//                .konto(4001)
//                .redBrojAkt(10)
//                .build();
//        ObrazacIODTO dtoExcel =  ObrazacIODTO.builder()
//                .izvorFin("01")
//                .funkKlas("200")
//                .konto(400111)
//                .redBrojAkt(10)
//                .build();
//
//        when(arhbudzetService.findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(3578, 2))
//                .thenReturn(List.of(arhbudzet));
//        when(mapper.toDtoFromArh(arhbudzet)).thenReturn(dtoFromArh);
//        assertEquals("",service.checkIfStandKlasifFromExcelExistInFinPlana(List.of(dtoExcel),
//                3578,2));
//    }

    @Test
   void  shouldThrowWhencheckIfPlanAndIzvrsenjeAreZero() {

        ObrazacIODTO obr1 = new ObrazacIODTO(1, "A", 1001, "X", null, 0.0, 0.0);
        ObrazacIODTO obr2 = new ObrazacIODTO(1, "A", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr3 = new ObrazacIODTO(1, "A", 1003, "X", "X",10.0, 3.0);
        ObrazacIODTO obr4 = new ObrazacIODTO(1, "A", 1001, "X", "X",10.0, 5.0);
        ObrazacIODTO obr5 = new ObrazacIODTO(1, "b", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr6 = new ObrazacIODTO(2, "A", 1001, "X", "X",10.0, 3.0);
        var dtos = List.of(obr1, obr2,obr3,obr6);
        assertThrows(ObrazacException.class, () -> service.checkIfPlanAndIzvrsenjeAreZero(dtos));
    }
//
    @Test
    void  shouldNotThrowWhencheckIfPlanAndIzvrsenjeAreZero() {

        ObrazacIODTO obr1 = new ObrazacIODTO(1, "A", 1001, "X", null, 1.0, 1.0);
        ObrazacIODTO obr2 = new ObrazacIODTO(1, "A", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr3 = new ObrazacIODTO(1, "A", 1003, "X", "X",10.0, 3.0);
        ObrazacIODTO obr4 = new ObrazacIODTO(1, "A", 1001, "X", "X",10.0, 5.0);
        ObrazacIODTO obr5 = new ObrazacIODTO(1, "b", 1002, "X", "X",10.0, 3.0);
        ObrazacIODTO obr6 = new ObrazacIODTO(2, "A", 1001, "X", "X",10.0, 3.0);
        var dtos = List.of(obr1, obr2,obr3,obr6);
        assertDoesNotThrow( () -> service.checkIfPlanAndIzvrsenjeAreZero(dtos));
    }
    @Test
    void  shouldReturnPomObrazacWhenConvertZakListInPomObrazac() throws Exception {
        List<ZakljucniListDetails> zakljucniList = Arrays.asList(
                ( ZakljucniListDetails.builder().KONTO(123456).DUGUJE_PS(1000.0).POTRAZUJE_PS(800.0)
                         .DUGUJE_PR(1200.0).POTRAZUJE_PR(900.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434567).DUGUJE_PS(3500.0).POTRAZUJE_PS(600.0)
                        .DUGUJE_PR(1100.0).POTRAZUJE_PR(1000.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434568).DUGUJE_PS(5500.0).POTRAZUJE_PS(500.0)
                        .DUGUJE_PR(1500.0).POTRAZUJE_PR(500.0).build())
        );
        var zakljucniListZb = new ZakljucniListZb();
        zakljucniListZb.setStavke(zakljucniList);
        when(zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(anyInt(), anyInt()))
                .thenReturn(Optional.of(zakljucniListZb));

        List<PomObrazac> pomObrazacList = Arrays.asList(
               new PomObrazac(434567, 3000.0) ,
                new PomObrazac(434568, 6000.0) );

        assertEquals(service.convertZakListInPomObrazac(1,1, 400000),pomObrazacList);
    }

    @Test
    void  shouldReturnPomObrazacWhenConvertIoToPomObrazac() {
        List<ObrazacIODTO> ios = Arrays.asList(
                new ObrazacIODTO(1, "ABC", 123456,"ABC", "ABC", 10000.0, 8000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 2000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 4000.0)
        );

        List<PomObrazac> pomObrazacListUnique = Arrays.asList(
                new PomObrazac(434567, 7000.0) ,
                new PomObrazac(434568, 6000.0) );

        assertEquals(service.convertIoToPomObrazac(ios, 400000), pomObrazacListUnique);
    }

    @Test
    void  shouldReturnPomObrazacWhenMakeListOfPomUniqueKontosAndSumOfSaldo() {
        List<PomObrazac> pomObrazacList = Arrays.asList(
                new PomObrazac(434567, 7000.0) ,
                new PomObrazac(434568, 6000.0),
                new PomObrazac(434567, 100.0) ,
                new PomObrazac(434568, 100.0)
        );
        List<PomObrazac> act = Arrays.asList(
                new PomObrazac(434567, 7100.0) ,
                new PomObrazac(434568, 6100.0)
        );
        assertEquals(service.makeListOfPomUniqueKontosAndSumOfSaldo(pomObrazacList), act);
    }

//    @Test
//    void shouldPassIfEqulaWhenChekEquality() throws Exception {
//        List<PomObrazac> act = Arrays.asList(
//                new PomObrazac(434567, 7100.0) ,
//                new PomObrazac(434568, 6100.0)
//        );
//        List<PomObrazac> exp = Arrays.asList(
//                new PomObrazac(434567, 7100.0) ,
//                new PomObrazac(434568, 6100.0)
//        );
//        assertDoesNotThrow(() -> service.chekEquality(act,exp));
//    }

//    @Test
//    void shouldThrowIfNotEqulaWhenChekEquality() throws Exception {
//        List<PomObrazac> act = Arrays.asList(
//                new PomObrazac(434567, 7100.0) ,
//                new PomObrazac(434568, 6100.0)
//        );
//        List<PomObrazac> exp = Arrays.asList(
//                new PomObrazac(434567, 7100.0) ,
//                new PomObrazac(434568, 6000.0)
//              //  new PomObrazac(434569, 6100.0)
//        );
//        assertThrowsExactly(ObrazacException.class, () -> service.chekEquality(act,exp));
//    }

    @Test
    void shouldNotThrowIfEqulaWhenCompareIoAndZakljucni() throws Exception {
        List<ObrazacIODTO> ios = Arrays.asList(
                new ObrazacIODTO(1, "ABC", 123456,"ABC", "ABC", 10000.0, 8000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 2000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 4000.0),
                new ObrazacIODTO(3, "GHI", 434569,"ABC", "ABC", 15000.0, 0.0)

        );
        List<ZakljucniListDetails> zakljucniList = Arrays.asList(
                ( ZakljucniListDetails.builder().KONTO(123456).DUGUJE_PS(1000.0).POTRAZUJE_PS(800.0)
                        .DUGUJE_PR(1200.0).POTRAZUJE_PR(900.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434567).DUGUJE_PS(3600.0).POTRAZUJE_PS(100.0)
                        .DUGUJE_PR(3600.0).POTRAZUJE_PR(100.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434568).DUGUJE_PS(5500.0).POTRAZUJE_PS(500.0)
                        .DUGUJE_PR(1500.0).POTRAZUJE_PR(500.0).build())
        );
        var zakljucniListZb = new ZakljucniListZb();
        zakljucniListZb.setStavke(zakljucniList);
        when(zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(anyInt(), anyInt()))
                .thenReturn(Optional.of(zakljucniListZb));

        assertDoesNotThrow(() -> service.compareIoAndZakljucni(ios,1,1));
    }

    @Test
    void shouldThrowIfNotEqulaWhenCompareIoAndZakljucni() throws Exception {
        List<ObrazacIODTO> ios = Arrays.asList(
                new ObrazacIODTO(1, "ABC", 123456,"ABC", "ABC", 10000.0, 8000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 2000.0),
                new ObrazacIODTO(2, "DEF", 434567, "ABC", "ABC",20000.0, 3500.0),
                new ObrazacIODTO(3, "GHI", 434568,"ABC", "ABC", 15000.0, 4000.0)
        );
        List<ZakljucniListDetails> zakljucniList = Arrays.asList(
                ( ZakljucniListDetails.builder().KONTO(123456).DUGUJE_PS(1000.0).POTRAZUJE_PS(800.0)
                        .DUGUJE_PR(1200.0).POTRAZUJE_PR(900.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434567).DUGUJE_PS(3500.0).POTRAZUJE_PS(100.0)
                        .DUGUJE_PR(3600.0).POTRAZUJE_PR(100.0).build()),
                ( ZakljucniListDetails.builder().KONTO(434568).DUGUJE_PS(5500.0).POTRAZUJE_PS(500.0)
                        .DUGUJE_PR(1500.0).POTRAZUJE_PR(500.0).build())
        );
        var zakljucniListZb = new ZakljucniListZb();
        zakljucniListZb.setStavke(zakljucniList);
        when(zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(anyInt(), anyInt()))
                .thenReturn(Optional.of(zakljucniListZb));

        assertThrowsExactly(ObrazacException.class,() -> service.compareIoAndZakljucni(ios,1,1));
    }


    @Test
    void testCheckIfAllKontosFromIoExistInZk_AllKontosExist() {
        List<PomObrazac> zak = List.of(new PomObrazac(400001, 2000.0), new PomObrazac(400002, 3000.0));
        List<PomObrazac> io = List.of(new PomObrazac(400001, 1500.0), new PomObrazac(400002, 2500.0));

        assertDoesNotThrow(() -> service.checkIfAllKontosFromIoExistInZk(zak, io));
    }

    @Test
    void testCheckIfAllKontosFromIoExistInZk_MissingKontoInZak() {
        List<PomObrazac> zak = List.of(new PomObrazac(400002, 2000.0));
        List<PomObrazac> io = List.of(new PomObrazac(400001, 1500.0), new PomObrazac(400002, 2500.0));

        ObrazacException exception = assertThrows(ObrazacException.class,
                () -> service.checkIfAllKontosFromIoExistInZk(zak, io));

        assertTrue(exception.getMessage().contains("Obrazac IO ima konta koja \n ne postoje u vec ucitanom Zakljucnom listu"));
    }

    @Test
    void testCheckIfAllKontosFromIoExistInZk_MissingKontoInIo() {
        List<PomObrazac> zak = List.of(new PomObrazac(400001, 2000.0), new PomObrazac(400002, 3000.0));
        List<PomObrazac> io = List.of(new PomObrazac(400001, 1500.0));

        ObrazacException exception = assertThrows(ObrazacException.class,
                () -> service.checkIfAllKontosFromIoExistInZk(zak, io));

        assertTrue(exception.getMessage().contains("Vec ucitani Zakljucni list ima konta\n koja ne postoje u obrascu IO"));
    }

    @Test
    void testCheckIfAllKontosFromIoExistInZk_MixedKonto4_7() {
        List<PomObrazac> zak = List.of(new PomObrazac(300001, 2000.0), new PomObrazac(500000, 3000.0));
        List<PomObrazac> io = List.of(new PomObrazac(300001, 1500.0));

        ObrazacException exception = assertThrows(ObrazacException.class,
                () -> service.checkIfAllKontosFromIoExistInZk(zak, io));

        assertTrue(exception.getMessage().contains("Vec ucitani Zakljucni list ima konta\n koja ne postoje u obrascu IO"));
    }
    @Test
    void testCheckIfAllKontosFromYlExistInZIo_AllKontosExist() {
        List<PomObrazac> zak = List.of(new PomObrazac(400001, 2000.0), new PomObrazac(400002, 3000.0), new PomObrazac(300000, 3000.0));
        List<PomObrazac> io = List.of(new PomObrazac(400001, 1500.0), new PomObrazac(400002, 2500.0));

        assertDoesNotThrow(() -> service.checkIfAllKontosFromIoExistInZk(zak, io));
    }
    @Test
    void shouldTransformToUniqueList() {
        List<PomObrazac> io = List.of(
                new PomObrazac(400001, 2000.0),
                new PomObrazac(400002, 3000.0),
                new PomObrazac(400001, 3000.0)
        );
        List<PomObrazac> expectedIo = List.of(
                new PomObrazac(400001, 5000.0),
                new PomObrazac(400002, 3000.0)
        );

        Assertions.assertThat(service.transformToUniqueList(io))
                .containsExactlyInAnyOrderElementsOf(expectedIo);
    }

    @Test
    void shouldNotThrowIfChekEqualityOfIoAndZlBySaldo() {

        List<PomObrazac> io = List.of(new PomObrazac(400001, 2000.0),
                new PomObrazac(400002, 3000.0),
                new PomObrazac(300002, 3000.0),

                new PomObrazac(400001, 3000.0));
        List<PomObrazac> zak = List.of(new PomObrazac(400001, 5000.0), new PomObrazac(400002, 3000.0));

        assertDoesNotThrow(() -> service.chekEqualityOfIoAndZlBySaldo(zak, io));
    }

    @Test
    void shouldThrowIfChekEqualityOfIoAndZlBySaldo() {

        List<PomObrazac> io = List.of(
                new PomObrazac(400001, 2000.0),
                new PomObrazac(400002, 3000.0),
                new PomObrazac(400001, 3000.0)
        );
        List<PomObrazac> zak = List.of(
                new PomObrazac(400001, 1000.0),
                new PomObrazac(400002, 3000.0)
        );

        ObrazacException exception = assertThrows(ObrazacException.class, () -> {
            service.chekEqualityOfIoAndZlBySaldo(zak, io);
        });

        String expectedMessage = "Obrazac IO i vec ucitani Zakljucni list \n" +
                "se ne slazu u kontima : 400001, ";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldNotThrowCheckKlasa3InIoExistAndIsSmallerThenInZakList() {

        List<PomObrazac> io = List.of(new PomObrazac(300001, 2000.0),
                                      new PomObrazac(300002, 3000.0));
        List<PomObrazac> zak = List.of(new PomObrazac(300001, 5000.0),
                                       new PomObrazac(300002, 3000.0));

        assertDoesNotThrow(() -> service.checkKlasa3InIoExistAndIsSmallerThenInZakList(io, zak));
    }

    @Test
    void shouldThrowCheckKlasa3InIoExistAndIsSmallerThenInZakList() {

        List<PomObrazac> io = List.of(new PomObrazac(300001, 6000.0),
                new PomObrazac(300002, 3000.0));
        List<PomObrazac> zak = List.of(new PomObrazac(300001, 5000.0),
                new PomObrazac(300002, 3000.0));

        ObrazacException exception = assertThrows(ObrazacException.class, () -> {
            service.checkKlasa3InIoExistAndIsSmallerThenInZakList(io, zak);
        });

        String expectedMessage = "Konto 300001 u Obrascu IO ima vecu vrednost \n od istog konta u vec ucitanom Zakljucnom listu!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));    }

    @Test
    void shouldThrowObrazacExceptionWhenIoSaldoIsGreaterThanZakSaldo() {
        PomObrazac io1 = PomObrazac.builder().konto(300001).saldo(100.00).build();
        PomObrazac zak1 = PomObrazac.builder().konto(300001).saldo(50.00).build(); // IO saldo is greater

        List<PomObrazac> ioKlasa3 = Arrays.asList(io1);
        List<PomObrazac> zakKlasa3 = Arrays.asList(zak1);

        Exception exception = assertThrows(ObrazacException.class, () -> {
            service.checkKlasa3InIoExistAndIsSmallerThenInZakList(ioKlasa3, zakKlasa3);
        });

        assertTrue(exception.getMessage().contains("Konto 300001 u Obrascu IO ima vecu vrednost \n od istog konta u vec ucitanom Zakljucnom listu!"));
    }

    @Test
    void shouldNotThrowWhenIoSaldoIsLessThanOrEqualZakSaldo() {
        PomObrazac io1 = PomObrazac.builder().konto(300001).saldo(50.00).build();
        PomObrazac zak1 = PomObrazac.builder().konto(300001).saldo(50.00).build();

        List<PomObrazac> ioKlasa3 = Arrays.asList(io1);
        List<PomObrazac> zakKlasa3 = Arrays.asList(zak1);

        assertDoesNotThrow(() -> {
            service.checkKlasa3InIoExistAndIsSmallerThenInZakList(ioKlasa3, zakKlasa3);
        });
    }

    @Test
    void shouldNotThrowWhenSaldoKonto311712And321311InWithinTolerance() {
        // Given
        PomObrazac zakEntry = new PomObrazac(311712, 100.0000);
        PomObrazac ioEntry = new PomObrazac(311712, 100.000001);
        List<PomObrazac> zakKlasa3 = Arrays.asList(zakEntry);
        List<PomObrazac> ioKlasa3 = Arrays.asList(ioEntry);

        // When & Then
        assertDoesNotThrow(() -> service.checkForKonto311712And321311InZakAndIo(zakKlasa3, ioKlasa3));
    }

    @Test
    void shouldThrowWhenSaldoKonto311712And321311OutsideTolerance() {
        // Given
        PomObrazac zakEntry = new PomObrazac(311712, 100.00);
        PomObrazac ioEntry = new PomObrazac(311712, 101.00);
        List<PomObrazac> zakKlasa3 = Arrays.asList(zakEntry);
        List<PomObrazac> ioKlasa3 = Arrays.asList(ioEntry);

        // When & Then
        Exception exception = assertThrows(ObrazacException.class, () ->
                service.checkForKonto311712And321311InZakAndIo(zakKlasa3, ioKlasa3));
        assertTrue(exception.getMessage().contains("ima razlicitu vrednost"));
    }

    @Test
    void shouldThrowWhenSaldoKonto311712And321311WhenMissingKontoInIO() {
        // Given
        PomObrazac zakEntry = new PomObrazac(311712, 100.00);
        List<PomObrazac> zakKlasa3 = Arrays.asList(zakEntry);
        List<PomObrazac> ioKlasa3 = Arrays.asList(); // Empty list

        // When & Then
        Exception exception = assertThrows(ObrazacException.class, () ->
                service.checkForKonto311712And321311InZakAndIo(zakKlasa3, ioKlasa3));
        assertTrue(exception.getMessage().contains("ne postoji u Obrasca IO"));
    }

    @Test
    void shouldNotThrowWhenEmptyListsInSaldoKonto311712And321311() {
        // Given
        List<PomObrazac> zakKlasa3 = Arrays.asList();
        List<PomObrazac> ioKlasa3 = Arrays.asList();

        // When & Then
        assertDoesNotThrow(() -> service.checkForKonto311712And321311InZakAndIo(zakKlasa3, ioKlasa3));
    }
}