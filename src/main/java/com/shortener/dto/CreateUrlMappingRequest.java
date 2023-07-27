package com.shortener.dto;

import java.util.Date;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUrlMappingRequest {

    @NotNull
    private String longUrl;

    private Date expiryTime;

    private String customShortUrl;

}
