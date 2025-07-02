package com.dasolsystem.core.post.documentpost.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.DocumentPost;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.Post;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.post.documentpost.dto.DocumentPostRequestDto;
import com.dasolsystem.core.post.documentpost.dto.DocumentPostResponseDto;
import com.dasolsystem.core.post.documentpost.repository.DocumentPostRepository;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import com.dasolsystem.core.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentPostServiceImpl implements DocumentPostService {
    private final DocumentPostRepository documentPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long createDocumentPost(DocumentPostRequestDto dto) {
        // 1) 작성자(member) 조회
        Member member = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + dto.getStudentId()));

        // 2) Post 엔티티 생성
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .capacity(dto.getCapacity())
                .target(dto.getTarget())
                .member(member)
                .build();

        // 3) EventPost 엔티티에 Post 연결 및 추가 필드 세팅
        DocumentPost documentPost = DocumentPost.builder()
                .post(post)                // composition 매핑이라면 post 필드에
                .filePath(dto.getFilePath())
                .realLocation(dto.getRealLocation())
                .build();

        // 4) 저장 (JOINED 상속이든, composition이든 save만으로 두 테이블에 반영)
        DocumentPost saved = documentPostRepository.save(documentPost);

        // 5) 생성된 식별자 반환
        return saved.getPostId();
    }

    @Transactional
    public Long deleteDocumentPost(Long postId, String studentId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new DBFaillException(ApiState.ERROR_1001,"없는 게시글"));
        if(!post.getMember().getStudentId().equals(studentId)) throw new DBFaillException(ApiState.ERROR_1002,"작성자 식별 오류, 다시 로그인하세요.");
        Long return_id = post.getPostId();
        postRepository.delete(post);
        return return_id;
    }

    @Transactional
    public Long updateDocumentPost(DocumentPostRequestDto dto, Long postId, String studentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_1001, "없는 게시글"));

        if (!post.getMember().getStudentId().equals(studentId)) {
            throw new DBFaillException(ApiState.ERROR_1002, "작성자 식별 오류, 다시 로그인하세요.");
        }

        // 1) Post 필드: null 체크 후에만 업데이트
        if (StringUtils.hasText(dto.getTitle())) {
            post.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getContent())) {
            post.setContent(dto.getContent());
        }
        if (dto.getStartDate() != null) {
            post.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            post.setEndDate(dto.getEndDate());
        }
        if (dto.getCapacity() != 0) {
            post.setCapacity(dto.getCapacity());
        }
        if (StringUtils.hasText(dto.getTarget())) {
            post.setTarget(dto.getTarget());
        }
        // 2) EventPost 필드: null 체크 후에만 업데이트
        DocumentPost documentPost = post.getDocumentPost();
        if(StringUtils.hasText(dto.getFilePath())) {
            documentPost.setFilePath(dto.getFilePath());
        }
        if(StringUtils.hasText(dto.getRealLocation())) {
            documentPost.setRealLocation(dto.getRealLocation());
        }

        return post.getPostId();
    }


    @Transactional(readOnly = true)
    public DocumentPostResponseDto getDocumentPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new DBFaillException(ApiState.ERROR_1001,"없는 게시글"));
        return DocumentPostResponseDto.builder()
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .capacity(post.getCapacity())
                .target(post.getTarget())
                .filePath(post.getDocumentPost().getFilePath())
                .realLocation(post.getDocumentPost().getRealLocation())
                .build();
    }
}
