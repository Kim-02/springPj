package com.dasolsystem.core.trasaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Builder
public class ErrorResponseDto {

    private List<String> completeUser;
    private List<String> userFoundFail;

    /**
     * 이름 or 이름(학번)을 key로 사용
     * 이벤트 리스트를 포함
     */
    private Map<String, SelectedUserInfo> selectedUser;

    /**
     * 동명이인일 경우만 studentIds 포함
     * 단일 사용자는 events만 포함
     */
    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SelectedUserInfo {
        private List<String> events;

        // 필요 시 확장 용도로 유지. 지금은 사용하지 않으니 null로 출력되지 않음.
        private List<String> studentIds;
    }
}
