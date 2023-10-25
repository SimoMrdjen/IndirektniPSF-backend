package IndirektniPSF.backend.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserDto mappUserToDto(User user) {
        return UserDto.builder()
                .sifraradnika(user.getSifraradnika())
                .za_sif_sekret(user.getZa_sif_sekret())
                .sif_oblast(user.getSif_oblast())
                .sifra_pp(user.getSifra_pp())
                .ime(user.getIme())
                .lozinka(user.getLozinka())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public User mapDtoToUser(UserDto dto) {
        return User.builder()
                .sifraradnika(dto.getSifraradnika())
                .za_sif_sekret(dto.getZa_sif_sekret())
                .sif_oblast(dto.getSif_oblast())
                .sifra_pp(dto.getSifra_pp())
                .ime(dto.getIme())
                .lozinka(dto.getLozinka())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .za_sif_rac(0)
                .build();
    }
}
