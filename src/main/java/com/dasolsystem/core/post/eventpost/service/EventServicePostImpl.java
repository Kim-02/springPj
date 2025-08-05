package com.dasolsystem.core.post.eventpost.service;

import com.dasolsystem.config.excption.DBFaillException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.*;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.post.eventpost.dto.EventItemDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;
import com.dasolsystem.core.post.eventpost.repository.EventPostRepository;
import com.dasolsystem.core.post.repository.PostRepository;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServicePostImpl implements EventPostService{
    private final EventPostRepository eventPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventParticipationRepository eventParticipationRepository;


    /**
     * 이벤트 게시글을 작성하는 서비스
     * 작성과 동시에 EventItem이 연결되고 만약 아이템이 없다면 그대로 들어간다.
     * 다중 선택 기능은 프론트에서 구현이 필요하다.
     * @param dto
     * @return
     */
    @Transactional
    public Long createEventPost(EventPostRequestDto dto) {
        // 1) Member 조회
        Member member = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2) Post 엔티티 생성 및 저장
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .capacity(dto.getCapacity())
                .target(dto.getTarget())
                .member(member)
                .build();
        postRepository.save(post);

        // 3) EventPost 생성
        EventPost eventPost = EventPost.builder()
                .post(post)
                .notice(dto.getNotice())
                .payAmount(dto.getPayAmount())
                .build();

        // 4) EventItem 들 연결
        for (EventItemDto itemDto : dto.getItems()) {
            EventItem item = EventItem.builder()
                    .itemName(itemDto.getItemName())
                    .itemCost(itemDto.getItemCost())
                    .build();
            eventPost.getEventItems().add(item);
        }

        // 5) 저장 (cascade로 EventItem들도 함께 persist)
        EventPost saved = eventPostRepository.save(eventPost);
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
        List<EventItem> newList = new ArrayList<>();
        for (EventItemDto itemDto : dto.getItems()) {
            EventItem item = EventItem.builder()
                    .itemName(itemDto.getItemName())
                    .itemCost(itemDto.getItemCost())
                    .build();
            newList.add(item);
        }
        eventPost.setEventItems(newList);
        return post.getPostId();
    }


    @Transactional(readOnly = true)
    public EventPostResponseDto getEventPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new DBFaillException(ApiState.ERROR_500,"없는 게시글"));
        List<EventItemDto> itemDto = new ArrayList<>();
        for(EventItem items: post.getEventPost().getEventItems()){
            itemDto.add(
                    EventItemDto.builder()
                            .itemName(items.getItemName())
                            .itemCost(items.getItemCost())
                            .id(items.getId())
                            .build()
            );
        }
        return EventPostResponseDto.builder()
                .memberName(post.getMember().getName())
                .content(post.getContent())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .capacity(post.getCapacity())
                .target(post.getTarget())
                .notice(post.getEventPost().getNotice())
                .payAmount(post.getEventPost().getPayAmount())
                .eventItem(itemDto)
                .build();
    }

    /**
     * 이벤트를 참여하기 위한 API 이벤트 참여 버튼을 누르면 호출
     * 참여시 해당 이벤트 전용 입금자명이 생성된다.
     * 한 유저는 이벤트에 한번만 참여 가능하다.
     * @param postId 해당 이벤트 ID
     * @param studentId 참여하는 memberId Token에서 추출
     * @return 저장된 참여 내역 정보 제목
     */
    @Transactional
    public String participateEventPost(Long postId, String studentId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"post를 찾을 수 없습니다."));
        Member user = userRepository.findByStudentId(studentId).orElseThrow(()->new DBFaillException(ApiState.ERROR_500,"유저 정보를 찾을 수 없습니다."));
        EventParticipationId id = new EventParticipationId();
        id.setPostId(post.getPostId());
        id.setMemberId(user.getMemberId());
        eventParticipationRepository.findByIdMemberIdAndIdPostId(
                user.getMemberId(), post.getPostId()
        ).ifPresent(ep ->{
            throw new DBFaillException(ApiState.ERROR_500,"이미 신청하였습니다.");
        });
        String baseName = post.getTitle() + user.getName();
        String paymentName = baseName;
        int suffix = (int)(user.getMemberId() % 10);

        while (eventParticipationRepository.existsByPaymentName(paymentName)) {
            paymentName = baseName + (suffix++);
        }

        EventParticipation eventParticipation =
                EventParticipation.builder()
                        .id(id)
                        .post(post)
                        .paidAt(null)
                        .paymentStatus(false)
                        .paymentName(paymentName)
                        .member(user)
                        .build();
        return eventParticipationRepository.save(eventParticipation).getPost().getTitle();
    }
}
