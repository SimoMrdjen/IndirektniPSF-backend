package IndirektniPSF.backend.arhbudzet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArhbudzetRepository extends JpaRepository<Arhbudzet, ArhbudzetId> {

    @Query("SELECT SUM(a.duguje) FROM Arhbudzet a JOIN a.izvor i WHERE a.sinKonto > 4000 AND a.sinKonto < 7000 " +
            "AND a.sifSekr = :sifSekr AND a.datum <= :date " +
            "AND a.jbbkIndKor = :jbbk AND i.kakva = 'budz'")
    Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date,  Integer jbbk);

    @Query("SELECT a FROM Arhbudzet a " +
            "WHERE a.vrstaPromene BETWEEN 500 AND 599 " +
            "AND a.jbbkIndKor = :jbbkIndKor " +
            "AND a.datum <= :datum " +
            "GROUP BY a.redBrojAkt, a.funkKlas, a.sinKonto, a.izvor")
    List<Arhbudzet> findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(
            @Param("jbbkIndKor") Integer jbbkIndKor,
            @Param("datum") Double datum);

    @Query("SELECT new Arhbudzet(a.sinKonto, a.izvor, a.redBrojAkt, a.funkKlas, SUM(a.duguje + a.duggtbr)) " +
            "FROM Arhbudzet a " +
            "WHERE a.jbbkIndKor = :jbbkIndKor AND a.datum <= :datum " +
            "AND a.sinKonto > 3999 AND a.sinKonto < 7000 " +
            "GROUP BY a.sinKonto, a.redBrojAkt, a.izvor, a.funkKlas")
    List<Arhbudzet> findByJbbkIndKorAndDatumLessThanEqualGroupByFields(
            @Param("jbbkIndKor") Integer jbbkIndKor,
            @Param("datum") Double datum);

    // TODO query for finding amount of plan for particular sinkonto
//    SELECT a.SIN_KONTO, a.RED_BROJ_AKT, a.IZVORFIN, a.FUNK_KLAS,
//    sum(A.DUGG+a.duggtbr) AS a.duguje
//    FROM
//    ARHBUDZET A
//    WHERE
//    A.JBBK_IND_KOR = ? AND
//    A.DATUM <= ?
//    and (a.SIN_KONTO>3999 and a.SIN_KONTO<7000)
//    group by  a.SIN_KONTO, a.RED_BROJ_AKT, a.IZVORFIN, a.FUNK_KLAS
}
