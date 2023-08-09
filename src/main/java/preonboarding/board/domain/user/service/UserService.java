package preonboarding.board.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import preonboarding.board.configuration.jwt.JwtProvider;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;
import preonboarding.board.global.exception.custom.DuplicateEmailException;
import preonboarding.board.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final String DEFAULT_USER_ROLE = "ROLE_USER";
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long signUp(UserSignUpRequest signUpRequest) {
        checkDuplicateEmail(signUpRequest.getEmail());

        User user = new User(
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );
        User savedUser = userRepository.save(user);
        return savedUser.getUserId();
    }

    @Transactional
    public String login(UserLoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 이메일 주소입니다."));
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("패스워드가 올바르지 않습니다.");
        }
        return jwtProvider.createToken(loginRequest.getEmail(), DEFAULT_USER_ROLE);
    }


    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }
}
