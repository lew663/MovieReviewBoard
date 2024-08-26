package com.board.movie.comment.controller;

import com.board.movie.comment.dto.CommentRequestDTO;
import com.board.movie.comment.dto.CommentResponseDTO;
import com.board.movie.comment.service.CommentService;
import com.board.movie.common.ApiResultDTO;
import com.board.movie.common.SuccessResponse;
import com.board.movie.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성
  @PostMapping("/comment/{postId}") // 게시글 id
  public ApiResultDTO<CommentResponseDTO> createComment(
      @PathVariable Long postId,
      @Valid @RequestBody CommentRequestDTO.CreateComment commentDto) {
    return commentService.createComment(postId, commentDto);
  }

  // 댓글 수정
  @PutMapping("/comment/{commentId}") // 댓글 id
  public ApiResultDTO<CommentResponseDTO> updateComment(
      @PathVariable Long commentId,
      @Valid @RequestBody CommentRequestDTO.CreateComment commentDto // create DTO와 동일해서 따로 추가안
  ) {
    return commentService.updateComment(commentId, commentDto);
  }

  // 댓글 삭제
  @DeleteMapping("/comment/{commentId}")
  public ApiResultDTO<SuccessResponse> deleteComment(
      @PathVariable Long commentId) {
    return commentService.deleteComment(commentId);
  }
}
