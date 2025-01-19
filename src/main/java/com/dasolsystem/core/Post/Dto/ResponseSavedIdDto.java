package com.dasolsystem.core.Post.Dto;


import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Description;


@Description("ID를 기준으로 질문에 접근하기 위한 Dto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSavedIdDto { //
    private Long savedId;
}
