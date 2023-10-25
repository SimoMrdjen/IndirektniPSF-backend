package IndirektniPSF.backend.security.auth;

import IndirektniPSF.backend.security.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private Integer sifraradnika;
    private Integer za_sif_sekret;
    private Integer sif_oblast;
    private Integer sifra_pp;
    private String email;
    private String password;
    private Role role;
}
