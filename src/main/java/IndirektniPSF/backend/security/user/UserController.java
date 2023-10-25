package IndirektniPSF.backend.security.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")

public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService
                .getAllUsers());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable(name = "id") Integer id
    ) {
        return ResponseEntity.ok(userService
                .getUser(id));
    }

    @GetMapping("/like")
    public ResponseEntity<List<UserDto>> getUserByUsernameLike(@RequestParam String UsernameLike) {
        return ResponseEntity.ok(userService
                .getUsersLike(UsernameLike));
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) throws Exception {
        return ResponseEntity.ok(userService
                .createUser(userDto));
    }

    @PutMapping//(value = "/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                      @RequestParam(name = "id") Integer id) throws Exception {

        return ResponseEntity.ok(userService
                .updateUser(userDto, id));
    }


}
