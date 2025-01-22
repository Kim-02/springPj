package com.dasolsystem.core.Post.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ResponseListDto {
    private Integer totalCount;
    private Integer page;
    private Integer pageSize;
}
