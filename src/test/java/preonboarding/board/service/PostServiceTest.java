package preonboarding.board.service;

// PostServiceTest.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import preonboarding.board.domain.post.Post;
import preonboarding.board.domain.post.service.PostService;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.post.dto.PostRequest;
import preonboarding.board.domain.post.dto.PostResponse;
import preonboarding.board.domain.post.dto.SimplePostResponse;
import preonboarding.board.domain.post.repository.PostRepository;
import preonboarding.board.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PostService postService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("게시글 생성: 정상 요청")
    public void createPost() {
        // Given
        String userEmail = "user@example.com";
        PostRequest request = new PostRequest("Title", "Content");
        User user = new User(userEmail, "123456789");
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(new Post(user, "Title", "Content"));

        // When
        PostResponse response = postService.createPost(userEmail, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("게시글 수정: 정상 요청에 의한 수정")
    public void updatePost() {
        // Given
        String userEmail = "user@example.com";
        Long postId = 1L;
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        User user = new User(userEmail, "123456789");
        Post post = new Post(user, "Title", "Content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        PostResponse response = postService.updatePost(request, userEmail, postId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("게시글 수정 실패: 다른 사용자의 접근")
    public void updatePostAccessDenied() {
        // Given
        String userEmail = "test@example.com";
        Long postId = 1L;
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        User user = new User("another@example.com", "123456789");
        Post post = new Post(user, "Title", "Content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When, Then
        assertThatThrownBy(() -> postService.updatePost(request, userEmail, postId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("모든 게시글 페이징 조회")
    public void findAll() {
        // Given
        int page = 0;
        int size = 1;
        User user = new User("test@test.com", "123456789");
        Post post = new Post(user, "Title", "Content");
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        Page<Post> postPage = new PageImpl<>(posts);
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        // When
        List<SimplePostResponse> responses = postService.findAll(page, size);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(post.getTitle());
        // 모든 페이지, 게시글 수 확인
        assertThat(postPage.getTotalElements()).isEqualTo(1);
        assertThat(postPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 게시글 조회: Id 통한 조회")
    public void findPostById() {
        // Given
        Long postId = 1L;
        User user = new User("test@test.com", "123456789");
        Post post = new Post(user, "Title", "Content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When
        PostResponse response = postService.findPostById(postId);

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("특정 게시글 조회 실패: 없는 게시글")
    public void findPostByIdNotFound() {
        // Given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> postService.findPostById(postId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("게시글 삭제: 정상 삭제")
    public void deletePostById() {
        // Given
        String userEmail = "test@example.com";
        Long postId = 1L;
        User user = new User(userEmail, "123456789");
        Post post = new Post(user, "Title", "Content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When, Then
        assertThatCode(() -> postService.deletePostById(userEmail, postId))
                .doesNotThrowAnyException();
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("게시글 삭제 실패: 다른 사용자의 접근")
    public void deletePostByIdAccessDenied() {
        // Given
        String userEmail = "test@example.com";
        Long postId = 1L;
        User user = new User("another@example.com", "123456789");
        Post post = new Post(user, "Title", "Content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // When, Then
        assertThatThrownBy(() -> postService.deletePostById(userEmail, postId))
                .isInstanceOf(AccessDeniedException.class);
        verify(postRepository, never()).deleteById(postId);
    }
}
