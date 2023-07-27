package com.shortener.controller;

import java.net.URI;
import java.util.Date;

import com.shortener.dto.CreateUrlMappingRequest;
import com.shortener.dto.CustomErrorMessages;
import com.shortener.dto.CustomErrorResponse;
import com.shortener.model.UrlMapping;
import com.shortener.service.UrlMappingService;
import com.shortener.utilities.ValidatorUtil;
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

    @PostMapping("/shortener")
    public ResponseEntity<Object> createUrlMapping(@RequestBody CreateUrlMappingRequest createUrlMappingRequest) {
        String originalUrl = createUrlMappingRequest.getLongUrl();
        Date expiryTime = createUrlMappingRequest.getExpiryTime();
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

    private ResponseEntity<Object> validateRequest(CreateUrlMappingRequest createUrlMappingRequest) {
        CustomErrorResponse customErrorResponse;
        String originalUrl = createUrlMappingRequest.getLongUrl();
        Date expiryTime = createUrlMappingRequest.getExpiryTime();

        if(null == originalUrl) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.URL_NOT_PRESENT.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        if(!ValidatorUtil.isValidUrl(originalUrl)) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.INVALID_URL_FORMAT.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }

        if( expiryTime.before(new Date())) {
            customErrorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), CustomErrorMessages.EXPIRY_TIME_IN_PAST.toString());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
