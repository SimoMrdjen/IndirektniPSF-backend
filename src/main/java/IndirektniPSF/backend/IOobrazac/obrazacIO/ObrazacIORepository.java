package IndirektniPSF.backend.IOobrazac.obrazacIO;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ObrazacIORepository extends JpaRepository<ObrazacIO, Integer> {

    @Transactional
    @Query(value = "select MAX(verzija)  from obrazac5_pom_zb o where o.jbbk_ind_kor = ?1 and koji_kvartal = ?2",
            nativeQuery = true)
    Optional<Integer> getLastVersionValue(Integer jbbk, Integer kvartal);

    @Query(value = "SELECT * FROM obrazac5_pom_zb WHERE JBBK_IND_KOR = :jbbkIndKor AND KOJI_KVARTAL = :kojiKvartal ORDER BY VERZIJA DESC LIMIT 1", nativeQuery = true)
    Optional<ObrazacIO> findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(@Param("jbbkIndKor") Integer jbbkIndKor, @Param("kojiKvartal") Integer kojiKvartal);


}
