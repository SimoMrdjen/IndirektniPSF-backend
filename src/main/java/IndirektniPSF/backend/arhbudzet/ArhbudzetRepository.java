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
//            "AND a.oznakaGlave = :glava " +
            "AND a.jbbkIndKor = :jbbk AND i.kakva = 'budz'")
    Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date,  Integer jbbk);

    @Query("SELECT a FROM Arhbudzet a " +
            "WHERE a.vrstaPromene BETWEEN 500 AND 599 " +
            "AND a.jbbkIndKor = :jbbkIndKor " +
//            "AND a.sifSekr = :sifSekr " +
            "AND a.datum <= :datum " +
            "GROUP BY a.redBrojAkt, a.funkKlas, a.sinKonto, a.izvor")
    List<Arhbudzet> findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(
            @Param("jbbkIndKor") Integer jbbkIndKor,
//            @Param("sifSekr") Integer sifSekr,
            @Param("datum") Double datum);

    //    List<Arhbudzet> findAllBySinKontoGreaterThanAndSinKontoLessThanAndSifSekrAndDatumLessThanEqualAndOznakaGlaveAndJbbkIndKorAndIzvorKakva(
//            Integer minSinKonto, Integer maxSinKonto, Integer sifSekr, Double date, Integer glava, Integer jbbk, String izvorKakva);

}
