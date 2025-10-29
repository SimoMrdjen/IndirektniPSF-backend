package IndirektniPSF.backend.idempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<Idempotency, UUID> {
}
