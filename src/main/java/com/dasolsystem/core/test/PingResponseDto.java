package com.dasolsystem.core.test;

import lombok.Builder;
import lombok.Data;

public class PingResponseDto {
    private final String pong = "pong";
    private final String timeStamp;

    public PingResponseDto(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPong(){
        return pong;
    }

    public String getTimeStamp(){
        return timeStamp;
    }
}
