package com.board.movie.post.service;

import com.board.movie.comment.entity.CommentEntity;
import com.board.movie.common.ApiResult;
import com.board.movie.common.ApiResultDTO;
import com.board.movie.common.SuccessResponse;
import com.board.movie.exception.CustomException;
import com.board.movie.exception.ErrorCode;
import com.board.movie.post.dto.PostRequestDTO;
import com.board.movie.post.dto.PostResponseDTO;
import com.board.movie.post.entity.PostEntity;
import com.board.movie.post.repository.PostRepository;
import com.board.movie.user.entity.UserEntity;
import com.board.movie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;

  // 전체 게시글 조회
  @Transactional
  public ApiResultDTO<List<PostResponseDTO.listPosts>> listPosts() {

    List<PostEntity> postList = postRepository.findAllByOrderByPostUpdatedDesc();    // postEntity 의 게시물 수정 시간을 기준으로 내림차순으로 조회

    // 게시물이 하나도 없을 경우
    if (postList == null || postList.isEmpty()) {
      return ApiResult.ok(Collections.emptyList());
    }

    List<PostResponseDTO.listPosts> postResponseDTOList = new ArrayList<>();
    for (PostEntity postEntity : postList) {
      PostResponseDTO.listPosts postResponseDTO = new PostResponseDTO.listPosts(postEntity);
      postResponseDTOList.add(postResponseDTO);
    }
    return ApiResult.ok(postResponseDTOList);
  }

  // 게시글 작성
  @Transactional
  public ApiResultDTO<PostResponseDTO> createPost(PostRequestDTO.CreatePost postDto) {

    // 현재 인증된 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    UserEntity userEntity = userRepository.findById(username)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      PostEntity postEntity = postDto.toEntity(userEntity);
      PostEntity savedPostEntity = postRepository.save(postEntity);
      PostResponseDTO postResponseDTO = new PostResponseDTO(savedPostEntity);
      return ApiResult.ok(postResponseDTO);
  }

  // 게시글 수정
  @Transactional
  public ApiResultDTO<PostResponseDTO> updatePost(Long postId, PostRequestDTO.UpdatePost postDto) {

    // 현재 인증된 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    UserEntity userEntity = userRepository.findById(username)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 게시글 조회
    PostEntity postEntity = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    // 게시글을 작성한 유저가 맞는지 확인
    Optional<PostEntity> post = postRepository.findByPostIdAndUser(postId, userEntity);
    if (post.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_WRITER);
    }

    // 게시글 정보 업데이트
    postEntity.setPostTitle(postDto.getPostTitle());
    postEntity.setPostMovieTitle(postDto.getPostMovieTitle());
    postEntity.setPostContent(postDto.getPostContent());

    PostResponseDTO responseDTO = new PostResponseDTO(postEntity);

    return ApiResult.ok(responseDTO);
  }

  // 게시글 삭제
  public ApiResultDTO<SuccessResponse> deletePost(Long postId) {

    // 현재 인증된 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    UserEntity userEntity = userRepository.findById(username)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 게시글 조회
    PostEntity postEntity = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    // 작성자 확인
    Optional<PostEntity> post = postRepository.findByPostIdAndUser(postId, userEntity);
    if (post.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_WRITER);
    }

    postRepository.delete(postEntity);
    return ApiResult.ok(SuccessResponse.of(HttpStatus.OK, "게시물 삭제 성공"));
  }

  // 선택한 게시물 조회
  @Transactional
  public ApiResultDTO<PostResponseDTO> getPost(Long postId) {
    // 게시글 조회
    PostEntity postEntity = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    // 댓글 작성일자 기준으로 내림차순 정렬
    List<CommentEntity> comments = postEntity.getCommentList();
    if (comments != null) {
      comments.sort(Comparator.comparing(
          CommentEntity::getCommentUpdated,
          Comparator.nullsLast(Comparator.naturalOrder())
      ).reversed());
    }

    // 조회수 증가
    postEntity.incrementViewCount();

    PostResponseDTO responseDTO = new PostResponseDTO(postEntity);

    return ApiResult.ok(responseDTO);
  }
}
