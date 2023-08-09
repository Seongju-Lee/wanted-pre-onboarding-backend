package preonboarding.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestValidationTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();


    @Test
    @DisplayName("회원가입 요청 DTO: 올바른 형식")
    public void testValidSignUpRequest() {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("test@test.com", "validPassword");
        // When
        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(signUpRequest);
        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("회원가입 요청 DTO: 이메일 형식 오류")
    public void testInvalidEmailSignUpRequest() {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("invalidEmail", "validPassword");
        // When
        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(signUpRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<UserSignUpRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("회원가입 요청 DTO: 비밀번호 형식 오류")
    public void testInvalidPasswordSignUpRequest() {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("validEmail@test.com", "invalid");
        // When
        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(signUpRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<UserSignUpRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("로그인 요청 DTO: 올바른 형식")
    public void testValidLoginRequest() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("validEmail@test.com", "validPassword");
        // When
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(loginRequest);
        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("로그인 요청 DTO: 이메일 형식 오류")
    public void testInvalidEmailLoginRequest() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("invalidEmail", "validPassword");
        // When
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(loginRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("로그인 요청 DTO: 비밀번호 형식 오류")
    public void testInvalidPasswordLoginRequest() {
        // Given
        UserLoginRequest loginRequest = new UserLoginRequest("validEmail@example.com", "invalid");
        // When
        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(loginRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<UserLoginRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
    }
}
