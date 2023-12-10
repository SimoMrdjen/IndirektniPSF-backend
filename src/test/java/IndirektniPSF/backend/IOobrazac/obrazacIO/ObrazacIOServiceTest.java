package IndirektniPSF.backend.IOobrazac.obrazacIO;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.exceptions.ObrazacException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ObrazacIOServiceTest {

    @InjectMocks
    ObrazacIOService service;

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
}