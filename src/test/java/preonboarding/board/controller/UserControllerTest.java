package preonboarding.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;
import preonboarding.board.global.exception.custom.DuplicateEmailException;
import preonboarding.board.domain.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = UserController.class, excludeFilters = {},
        includeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "preonboarding.board.configuration.jwt.*"
        ))
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("정상 회원가입")
    @WithMockUser(roles = {"SUPER"})
    public void signUp() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "12345678";
        // When
        mockMvc.perform(
                        post("/api/users/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new UserSignUpRequest(email, password)))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("정상 로그인")
    @WithMockUser(roles = {"SUPER"})
    public void login() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "12345678";
        // When
        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new UserLoginRequest(email, password)))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("회원가입 실패: 이메일 중복")
    @WithMockUser(roles = {"SUPER"})
    public void DuplicateEmail() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "12345678";
        UserSignUpRequest signUpRequest = new UserSignUpRequest(email, password);
        when(userService.signUp(any()))
                .thenThrow(new DuplicateEmailException());

        // When
        mockMvc.perform(
                        post("/api/users/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(signUpRequest))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 실패: 이메일 유효성 검증 실패")
    public void invalidEmail() throws Exception {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("testtest.com", "12345678");
        // When
        assertThat(signUpRequest).isNotNull();
        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(signUpRequest);
        // Then
        AssertionsForInterfaceTypes.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("회원가입 실패: 비밀번호 유효성 검증 실패")
    public void invalidPassword() throws Exception {
        // Given
        UserSignUpRequest signUpRequest = new UserSignUpRequest("test@example.com", "short");
        // When
        assertThat(signUpRequest).isNotNull();
        Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(signUpRequest);
        // Then
        AssertionsForInterfaceTypes.assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("로그인 실패: 없는 이메일")
    @WithMockUser(roles = {"SUPER"})
    public void nonExistentEmail() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "12345678";
        UserLoginRequest loginRequest = new UserLoginRequest(email, password);
        when(userService.login(any()))
                .thenThrow(new NoSuchElementException("없는 이메일입니다."));

        // When
        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(loginRequest))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("로그인 실패: 비밀번호 불일치")
    @WithMockUser(roles = {"SUPER"})
    public void wrongPassword() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "12345678";
        UserLoginRequest loginRequest = new UserLoginRequest(email, password);
        when(userService.login(any()))
                .thenThrow(new BadCredentialsException("비밀번호 불일치"));

        // When
        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(loginRequest))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}