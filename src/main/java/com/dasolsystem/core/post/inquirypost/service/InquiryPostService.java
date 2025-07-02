package com.dasolsystem.core.post.inquirypost.service;

import com.dasolsystem.core.post.inquirypost.dto.InquiryPostRequestDto;
import com.dasolsystem.core.post.inquirypost.dto.InquiryPostResponseDto;

public interface InquiryPostService {
    Long createInquiryPost(InquiryPostRequestDto dto);
    Long deleteInquiryPost(Long postId,String studentId);
    Long updateInquiryPost(InquiryPostRequestDto dto, Long postId, String studentId);
    InquiryPostResponseDto getInquiryPost(Long postId);
}
