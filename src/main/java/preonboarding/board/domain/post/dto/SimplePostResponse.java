package preonboarding.board.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import preonboarding.board.domain.post.Post;
import preonboarding.board.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class SimplePostResponse {
    private final Long postId;
    private final String userEmail;
    private final String title;

    public static SimplePostResponse fromEntity(Post post) {
        User user = post.getUser();
        return SimplePostResponse.builder()
                .postId(post.getPostId())
                .userEmail(user.getEmail())
                .title(post.getTitle())
                .build();
    }

    public static List<SimplePostResponse> fromEntities(List<Post> posts) {
        return posts.stream()
                .map(SimplePostResponse::fromEntity)
                .collect(Collectors.toList());
    }
}