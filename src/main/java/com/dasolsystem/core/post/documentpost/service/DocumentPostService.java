package com.dasolsystem.core.post.documentpost.service;

import com.dasolsystem.core.post.documentpost.dto.DocumentPostRequestDto;
import com.dasolsystem.core.post.documentpost.dto.DocumentPostResponseDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostRequestDto;
import com.dasolsystem.core.post.eventpost.dto.EventPostResponseDto;

import java.util.List;

public interface DocumentPostService {
    Long createDocumentPost(DocumentPostRequestDto dto);
    Long deleteDocumentPost(Long postId,String studentId);
    Long updateDocumentPost(DocumentPostRequestDto dto,Long postId,String studentId);
    DocumentPostResponseDto getDocumentPost(Long postId);
    List<DocumentPostResponseDto> getDocumentPosts();
}
