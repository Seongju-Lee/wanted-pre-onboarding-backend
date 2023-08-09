package preonboarding.board.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostRequest {
    @NotBlank(message = "게시물 제목은 필수 요소입니다.")
    private String title;
    @NotBlank(message = "게시글 내용은 필수 요소입니다.")
    private String content;
}
