package preonboarding.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import preonboarding.board.domain.post.dto.PostRequest;
import preonboarding.board.domain.post.dto.PostResponse;
import preonboarding.board.domain.post.dto.SimplePostResponse;
import preonboarding.board.domain.post.service.PostService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            Authentication authentication,
            @RequestBody @Valid PostRequest saveRequest
    ) {
        String userEmail = authentication.getName();
        PostResponse postResponse = postService.createPost(userEmail, saveRequest);
        String resourceUrl = String.format("/posts/%s", postResponse.getPostId());
        URI location = URI.create(resourceUrl);
        return ResponseEntity.created(location).body(postResponse);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            Authentication authentication,
            @RequestBody @Valid PostRequest updateRequest,
            @PathVariable Long postId
    ) {
        String email = authentication.getName();
        PostResponse postResponse = postService.updatePost(updateRequest, email, postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping
    public ResponseEntity<List<SimplePostResponse>> getPagingPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<SimplePostResponse> pagingPosts = postService.findAll(page, size);
        return ResponseEntity.ok(pagingPosts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        PostResponse post = postService.findPostById(postId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        String email = authentication.getName();
        postService.deletePostById(email, postId);
        return ResponseEntity.noContent().build();
    }
}