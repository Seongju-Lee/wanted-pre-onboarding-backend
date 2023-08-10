package preonboarding.board.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import preonboarding.board.domain.user.dto.Token;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;
import preonboarding.board.domain.user.service.UserService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(
            @RequestBody @Valid UserSignUpRequest signUpRequest
    ) {
        Long savedUserId = userService.signUp(signUpRequest);
        String resourceUrl = String.format("/users/%s", savedUserId);
        URI location = URI.create(resourceUrl);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(
            @RequestBody @Valid UserLoginRequest loginRequest
    ) {
        String token = userService.login(loginRequest);
        return ResponseEntity.ok(new Token(token));
    }
}