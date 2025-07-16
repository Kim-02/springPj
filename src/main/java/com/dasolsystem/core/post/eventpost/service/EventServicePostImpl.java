package com.dasolsystem.core.post.eventpost.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.*;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import com.dasolsystem.core.post.eventpost.repository.EventPostRepository;
import com.dasolsystem.core.post.repository.PostRepository;
import com.dasolsystem.core.user.dto.UserEventParticipationResponseDto;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EventServicePostImpl implements EventPostService{
    private final EventPostRepository eventPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventParticipationRepository eventParticipationRepository;
    @Transactional
    public Long createEventPost(EventPostRequestDto dto) {
        // 1) 작성자(member) 조회
        Member member = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. memberId=" + dto.getStudentId()));

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
        EventPost eventPost = EventPost.builder()
                .post(post)                // composition 매핑이라면 post 필드에
                .notice(dto.getNotice())   // 공지 여부
                .payAmount(dto.getPayAmount()) // 결제 금액
                .build();

        // 4) 저장 (JOINED 상속이든, composition이든 save만으로 두 테이블에 반영)
        EventPost saved = eventPostRepository.save(eventPost);

        // 5) 생성된 식별자 반환
        return saved.getPostId();
    }

    @Transactional
    public Long deleteEventPost(Long postId,String studentId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"없는 게시글"));
        if(!post.getMember().getStudentId().equals(studentId)) throw new DBFaillException(ApiState.ERROR_500,"작성자 식별 오류, 다시 로그인하세요.");
        Long return_id = post.getPostId();
        postRepository.delete(post);
        return return_id;
    }

    @Transactional
    public Long updateEventPost(EventPostRequestDto dto, Long postId, String studentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DBFaillException(ApiState.ERROR_500, "없는 게시글"));

        if (!post.getMember().getStudentId().equals(studentId)) {
            throw new DBFaillException(ApiState.ERROR_500, "작성자 식별 오류, 다시 로그인하세요.");
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
        EventPost eventPost = post.getEventPost();
        eventPost.setNotice(dto.getNotice());
        if (dto.getPayAmount() != 0) {
            eventPost.setPayAmount(dto.getPayAmount());
        }

        return post.getPostId();
    }


    @Transactional(readOnly = true)
    public EventPostResponseDto getEventPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new DBFaillException(ApiState.ERROR_500,"없는 게시글"));
        return EventPostResponseDto.builder()
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .capacity(post.getCapacity())
                .target(post.getTarget())
                .notice(post.getEventPost().getNotice())
                .payAmount(post.getEventPost().getPayAmount())
                .build();
    }

    /**
     * 이벤트를 참여하기 위한 API 이벤트 참여 버튼을 누르면 호출
     * @param postId 해당 이벤트 ID
     * @param studentId 참여하는 memberId Token에서 추출
     * @return 저장된 참여 내역 정보 제목
     */
    @Transactional
    public String participateEventPost(Long postId, String studentId) {
        EventParticipation eventParticipation =
                EventParticipation.builder()
                        .id(new EventParticipationId())
                        .post(postRepository.findById(postId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"post를 찾을 수 없습니다.")))
                        .paidAt(null)
                        .paymentStatus(false)
                        .member(userRepository.findByStudentId(studentId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"유저 정보를 찾을 수 없습니다.")))
                        .build();
        EventParticipation participateId = eventParticipationRepository.save(eventParticipation);
        return participateId.getPost().getTitle();
    }
}
