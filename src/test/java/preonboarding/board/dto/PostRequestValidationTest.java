package preonboarding.board.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import preonboarding.board.domain.post.dto.PostRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PostRequestValidationTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("게시물 작성 요청 DTO 검증 - 올바른 형식")
    public void testValidPostRequest() {
        // Given
        PostRequest postRequest = new PostRequest("Valid Title", "Valid Content");
        // When
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(postRequest);
        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("게시물 작성 요청 DTO 검증 - 제목 누락")
    public void testBlankTitlePostRequest() {
        // Given
        PostRequest postRequest = new PostRequest("", "Valid Content");
        // When
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(postRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<PostRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    @DisplayName("게시물 작성 요청 DTO 검증 - 내용 누락")
    public void testBlankContentPostRequest() {
        // Given
        PostRequest postRequest = new PostRequest("Valid Title", "");
        // When
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(postRequest);
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<PostRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("content");
    }
}
