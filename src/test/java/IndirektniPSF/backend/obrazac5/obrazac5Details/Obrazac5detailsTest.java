package IndirektniPSF.backend.obrazac5.obrazac5Details;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Obrazac5detailsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDifference() {
        // Create test data
        Obrazac5details obr5_1 = new Obrazac5details();
        obr5_1.setKonto(101);
        obr5_1.setRepublika(500.0);
        obr5_1.setPokrajina(300.0);
        obr5_1.setOpstina(200.0);
        obr5_1.setOoso(100.0);
        obr5_1.setDonacije(50.0);
        obr5_1.setOstali(30.0);

        Obrazac5details obr5_2 = new Obrazac5details();
        obr5_2.setKonto(102);
        obr5_2.setRepublika(400.0);
        obr5_2.setPokrajina(200.0);
        obr5_2.setOpstina(150.0);
        obr5_2.setOoso(90.0);
        obr5_2.setDonacije(60.0);
        obr5_2.setOstali(20.0);

        Obrazac5details obrIO_1 = new Obrazac5details();
        obrIO_1.setKonto(101);
        obrIO_1.setRepublika(100.0);
        obrIO_1.setPokrajina(50.0);
        obrIO_1.setOpstina(30.0);
        obrIO_1.setOoso(20.0);
        obrIO_1.setDonacije(10.0);
        obrIO_1.setOstali(5.0);

        Obrazac5details obrIO_2 = new Obrazac5details();
        obrIO_2.setKonto(103); // No match, should be ignored

        List<Obrazac5details> detailsObr5 = List.of(obr5_1, obr5_2);
        List<Obrazac5details> detailsFromObrIO = List.of(obrIO_1, obrIO_2);

        // Call the method
        List<Obrazac5details> result = Obrazac5details.difference(detailsObr5, detailsFromObrIO);

        // Verify results
        Obrazac5details resultObr5_1 = result.stream().filter(r -> r.getKonto() == 101).findFirst().orElse(null);
        Obrazac5details resultObr5_2 = result.stream().filter(r -> r.getKonto() == 102).findFirst().orElse(null);

        assertEquals(400.0, resultObr5_1.getRepublika());
        assertEquals(250.0, resultObr5_1.getPokrajina());
        assertEquals(170.0, resultObr5_1.getOpstina());
        assertEquals(80.0, resultObr5_1.getOoso());
        assertEquals(40.0, resultObr5_1.getDonacije());
        assertEquals(25.0, resultObr5_1.getOstali());

        // Verify that konto 102 remains unchanged
        assertEquals(400.0, resultObr5_2.getRepublika());
        assertEquals(200.0, resultObr5_2.getPokrajina());
        assertEquals(150.0, resultObr5_2.getOpstina());
        assertEquals(90.0, resultObr5_2.getOoso());
        assertEquals(60.0, resultObr5_2.getDonacije());
        assertEquals(20.0, resultObr5_2.getOstali());
    }
}