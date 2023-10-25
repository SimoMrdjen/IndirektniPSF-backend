package IndirektniPSF.backend.glavaSvi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GlavaSviRepository extends JpaRepository<GlavaSvi, Integer> {

    @Query(value = "SELECT * FROM GLAVASVI WHERE JED_BROJ_KORISNIKA = ?1 AND AKTIVAN = ?2", nativeQuery = true)
    Optional<GlavaSvi> findByJedBrojKorisnikaAndAktivan(Integer jedBrojKorisnika, Integer aktivan);
}
