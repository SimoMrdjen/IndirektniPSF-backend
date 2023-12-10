package IndirektniPSF.backend.arhbudzet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArhbudzetRepository extends JpaRepository<Arhbudzet, ArhbudzetId> {

}
