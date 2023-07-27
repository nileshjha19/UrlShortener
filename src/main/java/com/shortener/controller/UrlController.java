package com.shortener.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.shortener.dto.CreateCustomUrlMappingRequest;
import com.shortener.dto.CreateUrlMappingRequest;
import com.shortener.dto.CustomErrorMessages;
import com.shortener.dto.CustomErrorResponse;
import com.shortener.model.UrlConfigurations;
import com.shortener.model.UrlMapping;
import com.shortener.service.UrlConfigurationsService;
import com.shortener.service.UrlMappingService;
import com.shortener.utilities.ValidatorUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class UrlController {

    @Autowired
    UrlMappingService urlMappingService;

    @Autowired
    UrlConfigurationsService urlConfigurationsService;

    public static final String DEFAULT_EXPIRY_DAYS = "DEFAULT_EXPIRY_DAYS";

    public static final String MAX_CUSTOM_LENGTH = "MAX_CUSTOM_LENGTH";

    @PostMapping("/shortener")
    public ResponseEntity<Object> createUrlMapping(@RequestBody CreateUrlMappingRequest createUrlMappingRequest) {

        String originalUrl = createUrlMappingRequest.getLongUrl();
        Date expiryTime = createUrlMappingRequest.getExpiryTime();
        if(expiryTime == null) {
            String daysToAdd = urlConfigurationsService.findByConfigKey(DEFAULT_EXPIRY_DAYS);
            if(daysToAdd != null) {
                expiryTime = DateUtils.addDays(new Date(), Integer.parseInt(daysToAdd));
                createUrlMappingRequest.setExpiryTime(expiryTime);
            }
        }
        ResponseEntity<Object> responseResponseEntity = validateRequest(createUrlMappingRequest);
        if(responseResponseEntity != null)
            return responseResponseEntity;
        String shortenedUrl = urlMappingService.shortenUrl(originalUrl, expiryTime);
        if(null != shortenedUrl) {
            return ResponseEntity.ok(UrlMapping.builder().shortUrlKey(shortenedUrl).mainUrl(originalUrl).expiryTime(expiryTime).build());
        }
        return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/shortener/{shortUrlKey}")
    public ResponseEntity<Object> getAndRedirect(@PathVariable String shortUrlKey){
         String redirectUrl = urlMappingService.getOriginalUrl(shortUrlKey);
         HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.setLocation(URI.create(redirectUrl));
         return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

    @PostMapping("/shortener/custom")
    public ResponseEntity<Object> createCustomUrlMapping(@RequestBody CreateCustomUrlMappingRequest createCustomUrlMappingRequest) {
        String originalUrl = createCustomUrlMappingRequest.getLongUrl();
        Date expiryTime = createCustomUrlMappingRequest.getExpiryTime();
        String customUrl = createCustomUrlMappingRequest.getCustomUrl();

        ResponseEntity<Object> responseResponseEntity = validateCustomRequest(createCustomUrlMappingRequest);
        if(responseResponseEntity != null)
            return responseResponseEntity;

        String shortenedCustomUrl = urlMappingService.shortenCustomUrl(originalUrl, customUrl, expiryTime);
        if(null != shortenedCustomUrl) {
            return ResponseEntity.ok(UrlMapping.builder().shortUrlKey(shortenedCustomUrl).mainUrl(originalUrl).expiryTime(expiryTime).build());
        }
        return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);

    }
    private ResponseEntity<Object> validateRequest(CreateUrlMappingRequest createUrlMappingRequest) {
        String originalUrl = createUrlMappingRequest.getLongUrl();
        Date expiryTime = createUrlMappingRequest.getExpiryTime();
        return getObjectResponseEntity(originalUrl, expiryTime);
    }

    private ResponseEntity<Object>validateCustomRequest(CreateCustomUrlMappingRequest createCustomUrlMappingRequest) {

        CustomErrorResponse customErrorResponse;
        String originalUrl = createCustomUrlMappingRequest.getLongUrl();
        Date expiryTime = createCustomUrlMappingRequest.getExpiryTime();
        String customUrl = createCustomUrlMappingRequest.getCustomUrl();

        if(null == customUrl) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.URL_NOT_PRESENT.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        int maxCustomLength = Integer.parseInt(urlConfigurationsService.findByConfigKey(MAX_CUSTOM_LENGTH));

        if(customUrl.length() > maxCustomLength ) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.MAX_CUSTOM_LENGTH_BREACHED.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        return getObjectResponseEntity(originalUrl, expiryTime);

    }

    private ResponseEntity<Object> getObjectResponseEntity(String originalUrl, Date expiryTime) {
        CustomErrorResponse customErrorResponse;
        if(null == originalUrl) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.URL_NOT_PRESENT.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        if(!ValidatorUtil.isValidUrl(originalUrl)) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.INVALID_URL_FORMAT.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        if(expiryTime.before(new Date())) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.EXPIRY_TIME_IN_PAST.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
