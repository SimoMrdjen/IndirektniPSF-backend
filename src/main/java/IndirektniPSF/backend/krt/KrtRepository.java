package IndirektniPSF.backend.krt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KrtRepository extends JpaRepository<Krt, Integer> {
    @Query("SELECT k.sifRac FROM Krt k WHERE k.ulaziUKrt = 1 AND (k.datumGas = 0 OR k.datumGas IS NULL)" +
            " AND k.jedBrojKorisnika = :jedBrojKorisnika")
    List<Integer> findSifRacByUlaziUKrtAndDatumGasAndJedBrojKorisnika(@Param("jedBrojKorisnika") Integer jedBrojKorisnika);
}
