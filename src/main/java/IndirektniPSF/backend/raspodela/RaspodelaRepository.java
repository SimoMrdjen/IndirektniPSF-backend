package IndirektniPSF.backend.raspodela;

import IndirektniPSF.backend.arhbudzet.Arhbudzet;
import IndirektniPSF.backend.arhbudzet.ArhbudzetId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaspodelaRepository extends JpaRepository<Raspodela, RaspodelaId> {

}
