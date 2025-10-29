package IndirektniPSF.backend.IOobrazac;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObrazacIODTOTest {



    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testEquals() {
      var first =  new ObrazacIODTO(1, "A", 100, "X", "X", 10.0, 5.0);
       var sec =   new ObrazacIODTO(1, "A", 100, "X","X",  10.0, 3.0);
       var third = new ObrazacIODTO(2, "A", 100, "X","X", 10.0, 3.0);

       assertEquals(first, sec);
       assertNotEquals(sec, third);
    }


}