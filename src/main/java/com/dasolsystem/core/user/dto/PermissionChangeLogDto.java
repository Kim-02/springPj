package com.dasolsystem.core.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PermissionChangeLogDto {
    private String changeId;
    private String changeName;
    private String changeLog;
    private LocalDateTime changeTime;
}
