package com.dasolsystem.core.post.eventpost.service;

import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.EventPost;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.entity.Post;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.repository.EventPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class EventServicePostImpl implements EventPostService{
    private final EventPostRepository eventPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createEventPost(EventPostRequestDto dto) {
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
}
