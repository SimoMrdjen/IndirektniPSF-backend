package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.raspodela.Raspodela;
import IndirektniPSF.backend.raspodela.RaspodelaService;
import IndirektniPSF.backend.subkonto.SubkontoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ObrazacIODetailServiceTest {
    @InjectMocks
    ObrazacIODetailService service;
    @Mock
    SubkontoService subkontoService;
    @Mock
    RaspodelaService raspodelaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkIfKontosAreExistingExxludingSinKontos_NoException() {

        ObrazacIODTO obr1 = new ObrazacIODTO(1, "A", 100100, "X", null, null, null);
        ObrazacIODTO obr2 = new ObrazacIODTO(1, "A", 100222, "X", "X", 10.0, 3.0);
        ObrazacIODTO obr3 = new ObrazacIODTO(1, "A", 100344, "X", "X", 10.0, 3.0);
        List<ObrazacIODTO> dtos = List.of(obr1, obr2, obr3);
        List<Integer> kontos = List.of(100222, 100344);
        when(subkontoService.getKontniPlan()).thenReturn(kontos);

        assertDoesNotThrow(() -> service.checkIfKontosAreExistingExxludingSinKontos(dtos));
    }

    @Test
    void checkIfKontosAreExistingExxludingSinKontos_ThrowsException() {

        ObrazacIODTO obr = new ObrazacIODTO(1, "A", 999999, "X", "X", 10.0, 3.0);
        List<ObrazacIODTO> dtos = List.of(obr);
        List<Integer> kontos = List.of(100222, 100344);
        when(subkontoService.getKontniPlan()).thenReturn(kontos);

        assertThrows(ObrazacException.class,() -> service.checkIfKontosAreExistingExxludingSinKontos(dtos));
    }
    @Test
    void shouldAddFromWhoIsMoney() {

                ObrazacIODetails obrazacIODetails1 = ObrazacIODetails.builder()
                        .REDNI(2)
                        .obrazacIO(new ObrazacIO())  // Assuming ObrazacIO is another entity class
                        .IZVORFIN("0100")
                        .DUGUJE(200.00)
                        .POTRAZUJE(2000.00)
                        .REPUBLIKA(0.0)
                        .POKRAJINA(0.0)
                        .OPSTINA(0.0)
                        .OOSO(0.0)
                        .DONACIJE(0.0)
                        .OSTALI(0.0)
                        .build();

                // Creating the second instance of ObrazacIODetails
                ObrazacIODetails obrazacIODetails2 = ObrazacIODetails.builder()
                        .REDNI(2)
                        .obrazacIO(new ObrazacIO())  // Assuming ObrazacIO is another entity class
                        .IZVORFIN("0200")
                        .DUGUJE(3000.00)
                        .POTRAZUJE(2000.00)
                        .REPUBLIKA(0.0)
                        .POKRAJINA(0.0)
                        .OPSTINA(0.0)
                        .OOSO(0.0)
                        .DONACIJE(0.0)
                        .OSTALI(0.0)
                        .build();

        Raspodela raspodela1 = new Raspodela("0100", 6, 123, 1, 1);
        Raspodela raspodela2 = new Raspodela("0200", 7, 123, 1, 2);

        service.addFromWhoIsMoney(obrazacIODetails1, List.of(raspodela1, raspodela2));

        assertEquals(200, obrazacIODetails1.getREPUBLIKA());

            }

    @Test
    void shouldSetPropriateFieldAccordnigIzvor() {
        ObrazacIODetails obrazacIODetails1 = ObrazacIODetails.builder()
                .REDNI(2)
                .obrazacIO(new ObrazacIO())  // Assuming ObrazacIO is another entity class
                .IZVORFIN("0100")
                .DUGUJE(200.00)
                .POTRAZUJE(2000.00)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .OOSO(0.0)
                .DONACIJE(0.0)
                .OSTALI(0.0)
                .build();
        Raspodela raspodela1 = new Raspodela("0100", 6, 123, 1, 1);

        service.setPropriateFieldAccordnigIzvor(obrazacIODetails1, raspodela1);
        assertEquals(200, obrazacIODetails1.getREPUBLIKA() );

    }


    }