package IndirektniPSF.backend.raspodela;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RaspodelaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RaspodelaRepository raspodelaRepository;

    @BeforeEach
    void setUp() {
        // Setting up data
        Raspodela raspodela1 = new Raspodela("0100", 1, 1, 1, 1);
        Raspodela raspodela2 = new Raspodela("0200", 2, 2, 1, 2);
        Raspodela raspodela3 = new Raspodela("0200", 1, 2, 1, 3);
        Raspodela raspodela4 = new Raspodela("0300", 1, 3, 0, 3);

        entityManager.persist(raspodela1);
        entityManager.persist(raspodela2);
        entityManager.persist(raspodela3);
        entityManager.persist(raspodela4);
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    void shouldFindDistinctByIzvorFinAndIbkIsOne() {
        List<Raspodela> results = raspodelaRepository.findDistinctByIzvorFinAndIbkIsOne();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIzvorFin()).isEqualTo("0100");
    }
}
