package IndirektniPSF.backend.zakljucniList.zb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ZakljucniListZbRepository extends JpaRepository<ZakljucniListZb, Integer> {

    Optional<ZakljucniListZb> findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( Integer kvartal, Integer jbbks);
    Optional<ZakljucniListZb> findFirstByKojiKvartalAndJbbkIndKorOrderByGenMysqlDesc( Integer kvartal, Integer jbbks);
}
