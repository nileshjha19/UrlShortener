package com.shortener.dto;

public enum CustomErrorMessages {


    URL_NOT_PRESENT("Long URL not present"),
    INVALID_URL_FORMAT("Invalid URL format"),
    EXPIRY_TIME_IN_PAST("Expiry time in past"),
    CUSTOM_URL_NOT_PRESENT("Custom URL not present"),

    MAX_CUSTOM_LENGTH_BREACHED("Custom URL max length limit breached");


    private String value;

    CustomErrorMessages(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
