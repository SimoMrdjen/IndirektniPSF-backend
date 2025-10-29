package IndirektniPSF.backend.krt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StanjeKrtaRepository extends JpaRepository<StanjeKrta, Integer> {

    @Query("SELECT SUM(s.kumpot) FROM StanjeKrta s WHERE s.sifRac IN :accounts AND s.izvod = 0")
    Optional<Double> transferedAmountOfBalancesforJbbk(@Param("accounts") List<Integer> accounts);}
