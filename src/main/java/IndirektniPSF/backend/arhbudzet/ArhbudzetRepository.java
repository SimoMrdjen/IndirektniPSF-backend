package IndirektniPSF.backend.arhbudzet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArhbudzetRepository extends JpaRepository<Arhbudzet, ArhbudzetId> {

    @Query("SELECT SUM(a.duguje) FROM Arhbudzet a JOIN a.izvor i WHERE a.sinKonto > 4000 AND a.sinKonto < 7000 " +
            "AND a.sifSekr = :sifSekr AND a.datum <= :date " +
//            "AND a.oznakaGlave = :glava " +
            "AND a.jbbkIndKor = :jbbk AND i.kakva = 'budz'")
    Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date,  Integer jbbk);

    //    List<Arhbudzet> findAllBySinKontoGreaterThanAndSinKontoLessThanAndSifSekrAndDatumLessThanEqualAndOznakaGlaveAndJbbkIndKorAndIzvorKakva(
//            Integer minSinKonto, Integer maxSinKonto, Integer sifSekr, Double date, Integer glava, Integer jbbk, String izvorKakva);

}
