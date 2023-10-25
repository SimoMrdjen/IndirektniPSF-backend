package IndirektniPSF.backend.obrazac5.obrazacZb;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ObrazacZbRepository extends JpaRepository<ObrazacZb, Integer> {

    @Transactional
    @Query(value = "select MAX(verzija)  from obrazac_zb o where o.jbbk_ind_kor = ?1 and koji_kvartal = ?2",
            nativeQuery = true)
    Optional<Integer> getLastVersionValue(Integer jbbks, Integer kvartal);


}
