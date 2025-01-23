package com.dasolsystem.core.post.Dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ResponsePostListDto extends ResponseListDto{
    private List<ResponsePostDto> postList;

}
