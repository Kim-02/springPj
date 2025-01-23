package com.dasolsystem.core.post.Dto;

import jdk.jfr.Description;
import lombok.*;

@Description("클라이언트에서 받을 데이터를 저장하는 Dto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestRegisterPostDto {

    private String title;

    private String content;
}

