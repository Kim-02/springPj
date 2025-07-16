package com.dasolsystem.core.user.service;

import com.dasolsystem.core.entity.EventParticipation;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.user.dto.UserEventParticipationResponseDto;
import com.dasolsystem.core.user.dto.UserProfileResponseDto;
import com.dasolsystem.core.user.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final EventParticipationRepository eventParticipationRepository;

    @Transactional
    public UserProfileResponseDto getUserProfile(Member member) {
        return UserProfileResponseDto.builder()
                .email(member.getEmail())
                .paidUser(member.getPaidUser())
                .phone(member.getPhone())
                .gender(String.valueOf(member.getGender()))
                .name(member.getName())
                .studentId(member.getStudentId())
                .build();
    }

    @Transactional
    public List<UserEventParticipationResponseDto> getUserEventParticipation(Member member) {
        List<UserEventParticipationResponseDto> responseDtos = new ArrayList<>();
        List<EventParticipation> events = eventParticipationRepository.findByMemberMemberId(member.getMemberId());
        for(EventParticipation eventParticipation : events) {
            responseDtos.add(
                    UserEventParticipationResponseDto.builder()
                            .postId(eventParticipation.getPost().getPostId())
                            .postTitle(eventParticipation.getPost().getTitle())
                            .postContent(eventParticipation.getPost().getContent())
                            .postTarget(eventParticipation.getPost().getTarget())
                            .postStartDate(eventParticipation.getPost().getStartDate())
                            .postEndDate(eventParticipation.getPost().getEndDate())
                            .eventPayAmount(eventParticipation.getPost().getEventPost().getPayAmount())
                            .eventPaidDate(eventParticipation.getPaidAt())
                            .eventPaidSuccess(eventParticipation.getPaymentStatus())
                            .build()
            );
        }
        return responseDtos;
    }
}
