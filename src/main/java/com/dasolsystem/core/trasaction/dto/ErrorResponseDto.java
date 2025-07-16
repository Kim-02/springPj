package com.dasolsystem.core.trasaction.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ErrorResponseDto {
    private List<String> userFoundFail;
    private Map<String,List<String>> userDuplicate;
}
