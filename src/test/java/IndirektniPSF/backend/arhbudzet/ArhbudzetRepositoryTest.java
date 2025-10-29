package IndirektniPSF.backend.arhbudzet;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ArhbudzetRepositoryTest {

    @Autowired
    private ArhbudzetRepository arhbudzetRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Create instances of Arhbudzet
        Arhbudzet arhbudzet1 = new Arhbudzet(1, "123", 1L, 500.0, 1, 4500, 2023.0501, "G1", 2, null, 1, 1, 1, 100.0, 1);
        Arhbudzet arhbudzet2 = new Arhbudzet(2, "124", 2L, 300.0, 1, 4600, 2023.0401, "G2", 2, null, 1, 1, 1, 200.0, 1);
        Arhbudzet arhbudzet3 = new Arhbudzet(3, "125", 3L, 200.0, 1, 7100, 2023.0301, "G3", 2, null, 1, 1, 1, 300.0, 1);

        // Save instances to the database
        entityManager.persist(arhbudzet1);
        entityManager.persist(arhbudzet2);
        entityManager.persist(arhbudzet3);
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
    }

//    @Query("SELECT SUM(a.duguje) FROM Arhbudzet a JOIN a.izvor i WHERE a.sinKonto > 4000 AND a.sinKonto < 7000 " +
//            "AND a.sifSekr = :sifSekr AND a.datum <= :date " +
//            "AND a.jbbkIndKor = :jbbk AND i.kakva = 'budz'")
//    @Test
//    void testSumUplataIzBudzetaForIndKor() {
//        Integer sifSekr = 1;
//        Double date = 2023.0501;
//        Integer jbbk = 2;
//
//        Double result = arhbudzetRepository.sumUplataIzBudzetaForIndKor(sifSekr, date, jbbk);
//
//        // The expected sum is 500.0 + 300.0 = 800.0
//        assertEquals(800.0, result);
//
//    }

//    @Test
//    void findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene() {
//    }
//
//    @Test
//    void findByJbbkIndKorAndDatumLessThanEqualGroupByFields() {
//    }
}