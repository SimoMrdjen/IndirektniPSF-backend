package IndirektniPSF.backend.obrazac5.obrazac5;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface Obrazac5Repository extends JpaRepository<Obrazac5, Integer> {

    @Query(value = "SELECT * FROM obrazac_zb WHERE koji_kvartal = ?1 AND jbbk_ind_kor = ?2 ORDER BY verzija DESC LIMIT 1", nativeQuery = true)
    Optional<Obrazac5> findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(Integer kvartal, Integer jbbk);

    @Transactional
    @Query(value = "select MAX(verzija)  from obrazac_zb o where o.jbbk_ind_kor = ?1 and koji_kvartal = ?2",
            nativeQuery = true)
    Optional<Integer> getLastVersionValue(Integer jbbks, Integer kvartal);


}
