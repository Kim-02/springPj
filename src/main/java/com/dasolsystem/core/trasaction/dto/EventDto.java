package com.dasolsystem.core.trasaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    private String eventName;
    private String eventId;
}
