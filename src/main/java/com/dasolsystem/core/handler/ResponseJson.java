package com.dasolsystem.core.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseJson<T> {
    Integer status;
    String message;
    T result;

    @JsonIgnore
    Object trace;
    @JsonIgnore
    Object path;
}
