package com.shortener.dto;

import java.util.Date;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomUrlMappingRequest {

    @NotNull
    private String longUrl;

    private Date expiryTime;

    @NotNull
    private String customUrl;
}
