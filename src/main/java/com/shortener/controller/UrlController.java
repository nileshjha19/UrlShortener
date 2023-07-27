package com.shortener.controller;

import java.net.URI;
import java.util.Date;

import com.shortener.dto.CreateUrlMappingRequest;
import com.shortener.model.UrlMapping;
import com.shortener.service.UrlMappingService;
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
    public ResponseEntity<UrlMapping> createUrlMapping(@RequestBody CreateUrlMappingRequest createUrlMappingRequest) {
        String originalUrl = createUrlMappingRequest.getLongUrl();
        Date expiryTime = createUrlMappingRequest.getExpiryTime();
        if(null == originalUrl || null == expiryTime || expiryTime.before(new Date())) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UrlMapping.builder().mainUrl(originalUrl).expiryTime(expiryTime).build());
        }
        String shortenedUrl = urlMappingService.shortenUrl(originalUrl, expiryTime);
        if(null != shortenedUrl) {
            return ResponseEntity.ok(UrlMapping.builder().shortUrlKey(shortenedUrl).mainUrl(originalUrl).expiryTime(expiryTime).build());
        }
        return (ResponseEntity<UrlMapping>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/shortener/{shortUrlKey}")
    public ResponseEntity<Object> getAndRedirect(@PathVariable String shortUrlKey){
         String redirectUrl = urlMappingService.getOriginalUrl(shortUrlKey);
         HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.setLocation(URI.create(redirectUrl));
         return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

    }
}
