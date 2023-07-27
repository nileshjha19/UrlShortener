package com.shortener.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorResponse {

    private int status;
    private String message;
    private long timestamp;

    public CustomErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

}
