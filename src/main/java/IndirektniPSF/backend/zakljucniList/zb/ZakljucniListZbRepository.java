package IndirektniPSF.backend.zakljucniList.zb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZakljucniListZbRepository extends JpaRepository<ZakljucniListZb, Integer> {
    Optional<ZakljucniListZb> findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( Integer kvartal, Integer jbbks);
//        @Query("SELECT zl FROM ZakljucniListZb zl " +
//            "WHERE zl.KOJI_KVARTAL = :kvartal " +
//            "AND zl.JBBK_IND_KOR = :jbbkIndKor " +
//            "AND zl.VERZIJA = (SELECT MAX(zl2.VERZIJA) FROM ZakljucniListZb zl2 " +
//            "WHERE zl2.KOJI_KVARTAL = :kvartal AND zl2.JBBK_IND_KOR = :jbbkIndKor)")
//    Optional<ZakljucniListZb> findVerzijaByJbbksKvartal(
//            @Param("kvartal") Integer kvartal,
//            @Param("jbbkIndKor") Integer jbbkIndKor
//            );
Optional<ZakljucniListZb> findFirstByJbbkIndKorOrderByGenMysqlDesc(Integer jbbks);

}
