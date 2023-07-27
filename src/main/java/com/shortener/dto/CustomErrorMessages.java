package com.shortener.dto;

public enum CustomErrorMessages {


    URL_NOT_PRESENT("Long URL not present"),
    INVALID_URL_FORMAT("Invalid URL format"),
    EXPIRY_TIME_IN_PAST("Expiry time in past");


    private String value;

    CustomErrorMessages(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
