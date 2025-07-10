package com.dasolsystem.core.post.inquirypost.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.InquiryPost;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.Post;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.post.inquirypost.dto.InquiryPostRequestDto;
import com.dasolsystem.core.post.inquirypost.dto.InquiryPostResponseDto;
import com.dasolsystem.core.post.inquirypost.repository.InquiryPostRepository;
import com.dasolsystem.core.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InquiryPostServiceImpl implements InquiryPostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InquiryPostRepository inquiryPostRepository;


    @Transactional
    public Long createInquiryPost(InquiryPostRequestDto dto) {
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
        InquiryPost inquiryPost = InquiryPost.builder()
                .post(post)                // composition 매핑이라면 post 필드에
                .inquiryCode(dto.getInquiryCode())
                .build();

        // 4) 저장 (JOINED 상속이든, composition이든 save만으로 두 테이블에 반영)
        InquiryPost saved = inquiryPostRepository.save(inquiryPost);

        // 5) 생성된 식별자 반환
        return saved.getPostId();
    }
    @Transactional
    public Long deleteInquiryPost(Long postId,String studentId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"없는 게시글"));
        if(!post.getMember().getStudentId().equals(studentId)) throw new DBFaillException(ApiState.ERROR_500,"작성자 식별 오류, 다시 로그인하세요.");
        Long return_id = post.getPostId();
        postRepository.delete(post);
        return return_id;
    }
    @Transactional
    public Long updateInquiryPost(InquiryPostRequestDto dto, Long postId, String studentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "없는 게시글"));

        if (!post.getMember().getStudentId().equals(studentId)) {
            throw new DBFaillException(ApiState.ERROR_500, "작성자 식별 오류, 다시 로그인하세요.");
        }

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
        InquiryPost inquiryPost = post.getInquiryPost();
        inquiryPost.setInquiryCode(dto.getInquiryCode());

        return post.getPostId();
    }
    @Transactional(readOnly = true)
    public InquiryPostResponseDto getInquiryPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new DBFaillException(ApiState.ERROR_500,"없는 게시글"));
        return InquiryPostResponseDto.builder()
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .capacity(post.getCapacity())
                .target(post.getTarget())
                .inquiryCode(post.getInquiryPost().getInquiryCode())
                .build();
    }
}
