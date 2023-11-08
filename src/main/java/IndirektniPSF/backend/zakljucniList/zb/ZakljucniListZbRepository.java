package IndirektniPSF.backend.zakljucniList.zb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZakljucniListZbRepository extends JpaRepository<ZakljucniListZb, Integer> {
    Optional<ZakljucniListZb> findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( Integer kvartal, Integer jbbks);

Optional<ZakljucniListZb> findFirstByJbbkIndKorOrderByGenMysqlDesc(Integer jbbks);

}
