package preonboarding.board.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import preonboarding.board.domain.post.Post;
import preonboarding.board.domain.post.PostRepository;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 목록 조회: 페이징 처리")
    public void findAllPostsPaging() {
        // Given
        User user = userRepository.save(new User("test@example.com", "password"));
        for (int i = 0; i < 10; i++) {
            postRepository.save(new Post(user, "Title " + i, "Content " + i));
        }
        PageRequest pageRequest = PageRequest.of(0, 5);
        // When
        Page<Post> postPage = postRepository.findAll(pageRequest);
        // Then
        assertThat(postPage).isNotEmpty();
        assertThat(postPage.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("특정 게시글 조회: Id 통해 조회")
    public void findPostById() {
        // Given
        User user = userRepository.save(new User("test@example.com", "password"));
        Post post = postRepository.save(new Post(user, "Test Title", "Test Content"));
        // When
        Post foundPost = postRepository.findById(post.getPostId()).orElse(null);
        // Then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getPostId()).isEqualTo(post.getPostId());
    }

    @Test
    @DisplayName("게시글 저장")
    public void savePost() {
        // Given
        User user = userRepository.save(new User("test@example.com", "password"));
        Post post = new Post(user, "Title", "Content");
        // When
        Post savedPost = postRepository.save(post);
        // Then
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getPostId()).isNotNull();
    }

    @Test
    @DisplayName("특정 게시글 삭제: Id 통해 삭제")
    public void deletePostById() {
        // Given
        User user = userRepository.save(new User("test@example.com", "password"));
        Post post = postRepository.save(new Post(user, "Title", "Content"));
        // When
        postRepository.deleteById(post.getPostId());
        // Then
        Optional<Post> deletedPost = postRepository.findById(post.getPostId());
        assertThat(deletedPost.isEmpty()).isEqualTo(true);
    }
}
