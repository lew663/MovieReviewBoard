package com.board.movie.post.controller;

import com.board.movie.common.ApiResultDTO;
import com.board.movie.common.SuccessResponse;
import com.board.movie.config.security.UserDetailsImpl;
import com.board.movie.post.dto.PostRequestDTO;
import com.board.movie.post.dto.PostResponseDTO;
import com.board.movie.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

  private final PostService postService;

  // 전체 게시글 조회
  @GetMapping("/list")
  public ApiResultDTO<List<PostResponseDTO.listPosts>> listPosts() {
    return postService.listPosts();
  }

  // 게시글 작성
  @PostMapping
  public ApiResultDTO<PostResponseDTO> createPost(
      @Valid @RequestBody PostRequestDTO.CreatePost postDto) {
    return postService.createPost(postDto);
  }

  // 게시글 수정
  @PutMapping("/{postId}")
  public ApiResultDTO<PostResponseDTO> updatePost(
      @PathVariable("postId") Long postId,
      @Valid @RequestBody PostRequestDTO.UpdatePost postDto) {
    return postService.updatePost(postId, postDto);
  }

  // 게시글 삭제
  @DeleteMapping("/{postId}")
  public ApiResultDTO<SuccessResponse> deletePost(
      @PathVariable("postId") Long postId) {
    return postService.deletePost(postId);
  }

  // 선택한 게시물 조회
  @GetMapping("/{postId}")
  public ApiResultDTO<PostResponseDTO> getPost(
      @PathVariable("postId") Long postId) {
    return postService.getPost(postId);
  }
}
