package preonboarding.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import preonboarding.board.configuration.jwt.JwtProvider;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;
import preonboarding.board.domain.user.repository.UserRepository;
import preonboarding.board.domain.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService userService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("회원가입")
    public void signUp() {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("test@test.com", "password");
        User savedUser = new User("test@example.com", "encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        // When
        userService.signUp(signUpRequest);
        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("로그인: 정상 로그인")
    public void loginValidCredentials() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("test@test.com", "password");
        User user = new User("test@test.com", "encodedPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.createToken(anyString(), anyString())).thenReturn("token");

        // When
        String token = userService.login(loginRequest);

        // Then
        assertThat(token).isNotNull();
        verify(userRepository, times(1)).findByEmail(any());
        verify(jwtProvider, times(1)).createToken(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인: 잘못된 패스워드")
    public void testLoginInvalidCredentials() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("test@test.com", "invalidPassword");
        User user = new User("test@test.com", "encodedPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    @DisplayName("로그인: 등록되지 않은 이메일")
    public void testLoginNonExistentEmail() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("invalid@test.com", "password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}