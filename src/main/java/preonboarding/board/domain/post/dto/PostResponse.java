package preonboarding.board.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import preonboarding.board.domain.post.Post;
import preonboarding.board.domain.user.User;

@Getter
@RequiredArgsConstructor
@Builder
public class PostResponse {
    private final Long postId;
    private final String userEmail;
    private final String title;
    private final String content;

    public static PostResponse fromEntity(Post post) {
        User user = post.getUser();
        return PostResponse.builder()
                .postId(post.getPostId())
                .userEmail(user.getEmail())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
