package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.exceptions.ObrazacException;
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
}