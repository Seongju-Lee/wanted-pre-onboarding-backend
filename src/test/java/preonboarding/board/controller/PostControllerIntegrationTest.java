package preonboarding.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import preonboarding.board.domain.post.dto.PostRequest;
import preonboarding.board.domain.post.dto.PostResponse;
import preonboarding.board.domain.post.dto.SimplePostResponse;
import preonboarding.board.domain.user.dto.UserLoginRequest;
import preonboarding.board.domain.user.dto.UserSignUpRequest;
import preonboarding.board.domain.post.PostService;
import preonboarding.board.domain.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostService postService;
    @Autowired
    private UserService userService;
    private String authToken;
    private String authToken2;

    @BeforeEach
    public void setUp() {
        // 회원가입
        UserSignUpRequest signUpRequest = new UserSignUpRequest("test@example.com", "testpassword");
        userService.signUp(signUpRequest);
        UserSignUpRequest signUpRequest2 = new UserSignUpRequest("test2@example.com", "testpassword2");
        userService.signUp(signUpRequest2);
        // 로그인
        UserLoginRequest loginRequest = new UserLoginRequest("test@example.com", "testpassword");
        authToken = userService.login(loginRequest);
        UserLoginRequest loginRequest2 = new UserLoginRequest("test2@example.com", "testpassword2");
        authToken2 = userService.login(loginRequest2);

        SecurityContextHolder.clearContext(); // 현재 사용자 컨텍스트 초기화

    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext(); // 테스트 종료 후 사용자 컨텍스트 초기화
    }

    @Test
    @DisplayName("게시글 작성 가능: 로그인 상태 -> 작성 권한 있음")
    public void createPost() throws Exception {

        PostRequest request = new PostRequest("테스트 제목", "테스트 내용");
        PostResponse response = new PostResponse(1L, "test@example.com", "테스트 제목", "테스트 내용");

        when(postService.createPost(anyString(), any(PostRequest.class))).thenReturn(response);
        mockMvc.perform(
                        post("/api/posts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer "+ authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 작성 실패: 로그인 안한 상태 -> 작성 권한 없음")
    public void createPostWithoutLogin() throws Exception {

        // Given
        PostRequest request = new PostRequest("테스트 제목", "테스트 내용");
        // When & Then
        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 수정 가능: 본인이 작성한 게시글")
    public void updatePost() throws Exception {
        // Given
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        PostResponse response = new PostResponse(1L, "test@example.com", "Updated Title", "Updated Content");
        when(postService.updatePost(any(PostRequest.class), anyString(), anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(
                        patch("/api/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 수정 실패: 다른 사람이 작성한 게시글")
    public void updatePostOtherUser() throws Exception {
        // Given
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        when(postService.updatePost(any(PostRequest.class), anyString(), anyLong()))
                .thenThrow(new AccessDeniedException("수정 권한 없음"));
        // When & Then
        mockMvc.perform(
                        patch("/api/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("게시글 수정 실패: 로그인 안한 상태")
    public void updatePostWithoutLogin() throws Exception {
        // Given
        PostRequest request = new PostRequest("Updated Title", "Updated Content");
        // When & Then
        mockMvc.perform(
                        patch("/api/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 목록 페이징 조회")
    public void getPagingPosts() throws Exception {
        // Given
        List<SimplePostResponse> responses = Arrays.asList(
                new SimplePostResponse(1L, "testA@test.com", "titleA"),
                new SimplePostResponse(2L, "testB@test.com", "titleB")
        );
        when(postService.findAll(anyInt(), anyInt())).thenReturn(responses);
        // When & Then
        mockMvc.perform(
                        get("/api/posts")
                                .param("page", "1")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 게시글 조회")
    public void getPostById() throws Exception {
        // Given
        PostResponse response = new PostResponse(1L, "test@example.com", "Test Title", "Test Content");
        when(postService.findPostById(anyLong())).thenReturn(response);
        // When & Then
        mockMvc.perform(
                        get("/api/posts/1")
                            .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제")
    public void deletePost() throws Exception {
        mockMvc.perform(
                        delete("/api/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("게시글 삭제 실패: 로그인하지 않은 상태")
    public void deletePostWithoutLogin() throws Exception {
        mockMvc.perform(
                        delete("/api/posts/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 삭제 실패: 본인이 아닌 사용자의 게시글")
    public void deleteOtherUserPost() throws Exception {
        // Given
        doThrow(new AccessDeniedException("삭제 권한 없음"))
                .when(postService).deletePostById(anyString(), anyLong());
        // When & Then
        mockMvc.perform(
                        delete("/api/posts/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken2)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}