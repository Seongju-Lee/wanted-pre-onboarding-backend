package preonboarding.board.domain.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import preonboarding.board.domain.BaseTime;
import preonboarding.board.domain.user.User;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;
    private String content;


    public Post(User user, String title, String content) {
        validateContent(content);
        validateTitle(title);
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void updatePost(String title, String content) {
        if(!title.isEmpty()) {
            validateTitle(title);
            this.title = title;
        }
        if(!content.isEmpty()) {
            validateContent(content);
            this.content = content;
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목이 비어있습니다.");
        }
    }
    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글 내용을 입력해주세요.");
        }
    }
}