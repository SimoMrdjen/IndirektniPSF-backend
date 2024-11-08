package IndirektniPSF.backend.raspodela;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RaspodelaRepository extends JpaRepository<Raspodela, RaspodelaId> {
    @Query("SELECT r FROM Raspodela r WHERE r.ibk = 1 AND r.izvorFin IN " +
            "(SELECT r2.izvorFin FROM Raspodela r2 WHERE r2.ibk = 1 GROUP BY r2.izvorFin HAVING COUNT(r2.izvorFin) = 1)")
    List<Raspodela> findDistinctByIzvorFinAndIbkIsOne();

    @Query("SELECT r FROM Raspodela r WHERE r.ibk = 1 AND r.izvorFin IN " +
            "(SELECT r2.izvorFin FROM Raspodela r2 WHERE r2.ibk = 1 GROUP BY r2.izvorFin HAVING COUNT(r2.izvorFin) > 1)")
    List<Raspodela> findIzvorFinIfNotUnique();
}
