package com.shortener.service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.shortener.exception.ResourceAlreadyExistsException;
import com.shortener.exception.ResourceNotFoundException;
import com.shortener.model.UrlMapping;
import com.shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingService {

    private UrlRepository urlRepository;

    private UrlConfigurationsService urlConfigurationsService;


    @Autowired
    public void UrlMappingService(UrlRepository urlRepository, UrlConfigurationsService urlConfigurationsService) {
        this.urlRepository = urlRepository;
        this.urlConfigurationsService = urlConfigurationsService;
    }
    private final String BASE_URL = "https://tiney.com/";

    public static final String SHORT_URL_LENGTH = "SHORT_URL_LENGTH";


    public String shortenUrl(String originalUrl, Date expiryTime) {
        Optional<UrlMapping> existingMapping = Optional.ofNullable(urlRepository.findByMainUrl(originalUrl));
        if (existingMapping.isPresent() && (existingMapping.get().getExpiryTime().after(new Date()))) {
                existingMapping.get().setExpiryTime(expiryTime);
                urlRepository.save(existingMapping.get());
                return BASE_URL + existingMapping.get().getShortUrlKey();
        }

        if(existingMapping.isPresent() && (existingMapping.get().getExpiryTime().before(new Date()))) {
            String shortUrl = generateShortUrl();
            existingMapping.get().setExpiryTime(expiryTime);
            existingMapping.get().setShortUrlKey(shortUrl);
            urlRepository.save(existingMapping.get());
            return BASE_URL + existingMapping.get().getShortUrlKey();
        }
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = UrlMapping.builder().shortUrlKey(shortUrl).mainUrl(originalUrl).expiryTime(expiryTime).build();
        urlRepository.save(urlMapping);
        return BASE_URL + shortUrl;
    }

    public String shortenCustomUrl(String originalUrl, String customUrlKey, Date expiryTime) {
        Optional<UrlMapping> customUrlMapping = Optional.ofNullable(urlRepository.findByShortUrlKey(customUrlKey));
        if(customUrlMapping.isPresent()) {
            throw new ResourceAlreadyExistsException("The custom key already exists ");
        }

        Optional<UrlMapping> existingMapping = Optional.ofNullable(urlRepository.findByMainUrl(originalUrl));
        if (existingMapping.isPresent()) {
            existingMapping.get().setExpiryTime(expiryTime);
            existingMapping.get().setShortUrlKey(customUrlKey);
            urlRepository.save(existingMapping.get());
            return BASE_URL + existingMapping.get().getShortUrlKey();
        }

        UrlMapping urlMapping = UrlMapping.builder().shortUrlKey(customUrlKey).mainUrl(originalUrl).expiryTime(expiryTime).build();
        urlRepository.save(urlMapping);
        return BASE_URL + customUrlKey;
    }

    public String getOriginalUrl(String shortUrl) {
        Optional<UrlMapping> urlMapping = Optional.ofNullable(Optional.ofNullable(urlRepository.findByShortUrlKey(shortUrl))
            .orElseThrow(() -> new ResourceNotFoundException("There is no link to " + shortUrl)));

        if(urlMapping.isPresent()) {
            if(urlMapping.get().getExpiryTime() != null && urlMapping.get().getExpiryTime().before(new Date())) {
                throw new ResourceNotFoundException("Link Expired");
            }
        }

        return urlMapping.get().getMainUrl();
    }

    private String generateShortUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrlBuilder = new StringBuilder();
        int shortUrlLength = Integer.parseInt(urlConfigurationsService.findByConfigKey(SHORT_URL_LENGTH));

        for (int i = 0; i < shortUrlLength; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(characters.length());
            shortUrlBuilder.append(characters.charAt(randomIndex));
        }

        return shortUrlBuilder.toString();
    }

}
