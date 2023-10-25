package IndirektniPSF.backend.obrazac5.ppartner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PPartnerRepository extends JpaRepository<PPartner, Integer> {
    Optional<PPartner> findById(Integer sifra_pp) ;
}
