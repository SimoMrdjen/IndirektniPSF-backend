package IndirektniPSF.backend.idempotency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Idempotency {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column
    private String status;
    public Idempotency(UUID idempotencyKey, String status) {
        this.id = idempotencyKey;
        this.status = status;
    }
//    public Idempotency(java.util.UUID idempotencyKey, String processed) {
//    }
}
