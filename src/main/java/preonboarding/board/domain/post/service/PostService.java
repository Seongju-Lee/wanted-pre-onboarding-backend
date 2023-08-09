package preonboarding.board.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import preonboarding.board.domain.post.Post;
import preonboarding.board.domain.user.User;
import preonboarding.board.domain.post.dto.PostRequest;
import preonboarding.board.domain.post.dto.PostResponse;
import preonboarding.board.domain.post.dto.SimplePostResponse;
import preonboarding.board.global.util.Paging;
import preonboarding.board.domain.post.repository.PostRepository;
import preonboarding.board.domain.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(
            String userEmail,
            PostRequest saveRequest
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoSuchElementException("없는 사용자입니다."));
        Post savedPost = postRepository.save(
                new Post(user, saveRequest.getTitle(), saveRequest.getContent())
        );
        return PostResponse.fromEntity(savedPost);
    }

    @Transactional
    public PostResponse updatePost(
            PostRequest updateRequest,
            String userEmail,
            Long postId
    ) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("없는 게시글입니다."));

        if(foundPost.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            foundPost.updatePost(updateRequest.getTitle(), updateRequest.getContent());
            return PostResponse.fromEntity(foundPost);
        }
        throw new AccessDeniedException("파일 수정 권한이 없습니다.");
    }

    @Transactional(readOnly = true)
    public List<SimplePostResponse> findAll(int page, int size) {
        Page<Post> posts = postRepository.findAll(Paging.createPageRequest(page, size));
        return SimplePostResponse.fromEntities(posts.getContent());
    }

    @Transactional(readOnly = true)
    public PostResponse findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("없는 게시글입니다."));
        return PostResponse.fromEntity(post);
    }

    @Transactional
    public void deletePostById(String userEmail, Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("없는 게시글입니다."));

        if(!foundPost.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("파일 삭제 권한이 없습니다.");
        }
        postRepository.deleteById(postId);
    }
}