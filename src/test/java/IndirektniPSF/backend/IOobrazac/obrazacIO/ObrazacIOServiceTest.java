package IndirektniPSF.backend.IOobrazac.obrazacIO;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
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
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Test
   void testcheckIfStandKlasifFromExcelExistInFinPlana() throws ObrazacException {
        Arhbudzet arhbudzet = new Arhbudzet(1, "Nalog001", 100L, 5000.0, 30, 4001, 20200101.0,
                "Glava01", 3578,
                new Izvor("01","budz"), 10, 200, 1);
        ObrazacIODTO dtoFromArh =  ObrazacIODTO.builder()
                .izvorFin("01")
                .funkKlas("200")
                .konto(4001)
                .redBrojAkt(10)
                .build();
        ObrazacIODTO dtoExcel =  ObrazacIODTO.builder()
                .izvorFin("01")
                .funkKlas("200")
                .konto(400111)
                .redBrojAkt(10)
                .build();

        when(arhbudzetService.findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(3578, 2))
                .thenReturn(List.of(arhbudzet));
        when(mapper.toDtoFromArh(arhbudzet)).thenReturn(dtoFromArh);
        assertEquals("",service.checkIfStandKlasifFromExcelExistInFinPlana(List.of(dtoExcel),
                3578,2));
    }

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
}