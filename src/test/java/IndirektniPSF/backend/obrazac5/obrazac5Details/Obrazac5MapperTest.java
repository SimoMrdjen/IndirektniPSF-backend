package IndirektniPSF.backend.obrazac5.obrazac5Details;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Obrazac5MapperTest {

    @InjectMocks
    Obrazac5Mapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void mapIOtoObr5() {

    }

    @Test
    void testAggregateByKonto() {
        // Test data
        Obrazac5details item1 = new Obrazac5details();
        item1.setKonto(101);
        item1.setPokrajina(100.0);
        item1.setRepublika(200.0);
        item1.setOpstina(50.0);
        item1.setOoso(30.0);
        item1.setDonacije(20.0);
        item1.setOstali(20.0);


        Obrazac5details item2 = new Obrazac5details();
        item2.setKonto(101); // Same konto to trigger aggregation
        item2.setPokrajina(150.0);
        item2.setRepublika(100.0);
        item2.setOpstina(75.0);
        item2.setOoso(25.0);
        item2.setDonacije(15.0);
        item2.setOstali(20.0);


        Obrazac5details item3 = new Obrazac5details();
        item3.setKonto(101); // Different konto, should be a separate entry
        item3.setPokrajina(10.0);
        item3.setRepublika(10.0);
        item3.setOpstina(10.00);
        item3.setOoso(10.0);
        item3.setDonacije(10.00);
        item3.setOstali(20.0);

        Obrazac5details item4 = new Obrazac5details();
        item4.setKonto(102); // Different konto, should be a separate entry
        item4.setPokrajina(120.0);
        item4.setRepublika(80.0);
        item4.setOpstina(60.0);
        item4.setOoso(40.0);
        item4.setDonacije(30.0);
        item4.setOstali(20.0);


        List<Obrazac5details> detailsList = List.of(item1, item2, item3, item4);

        // Expected results
        List<Obrazac5details> aggregatedList = mapper.aggregateByKonto(detailsList);

        // Verify aggregation result
        assertEquals(2, aggregatedList.size());

        Obrazac5details aggregatedItem1 = aggregatedList.stream()
                .filter(item -> item.getKonto().equals(101))
                .findFirst()
                .orElse(null);

        Obrazac5details aggregatedItem2 = aggregatedList.stream()
                .filter(item -> item.getKonto().equals(102))
                .findFirst()
                .orElse(null);

        // Check that the aggregated values for konto 101 are as expected
        assertEquals(101, aggregatedItem1.getKonto());
        assertEquals(260.0, aggregatedItem1.getPokrajina());
        assertEquals(310.0, aggregatedItem1.getRepublika());
        assertEquals(135.0, aggregatedItem1.getOpstina());
        assertEquals(65.0, aggregatedItem1.getOoso());
        assertEquals(45.0, aggregatedItem1.getDonacije());
        assertEquals(60.0, aggregatedItem1.getOstali());


        // Check that the values for konto 102 remain as is
        assertEquals(102, aggregatedItem2.getKonto());
        assertEquals(120.0, aggregatedItem2.getPokrajina());
        assertEquals(80.0, aggregatedItem2.getRepublika());
        assertEquals(60.0, aggregatedItem2.getOpstina());
        assertEquals(40.0, aggregatedItem2.getOoso());
        assertEquals(30.0, aggregatedItem2.getDonacije());
    }
}