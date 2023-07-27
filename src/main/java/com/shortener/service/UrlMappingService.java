package com.shortener.service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.shortener.exception.ResourceNotFoundException;
import com.shortener.model.UrlMapping;
import com.shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingService {

    private UrlRepository urlRepository;

    @Autowired
    public void URLShorteningService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    private final String BASE_URL = "https://tiney.com/";
    private final int SHORT_URL_LENGTH = 6;

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

    public String getOriginalUrl(String shortUrl) {
        Optional<UrlMapping> urlMapping = Optional.ofNullable(Optional.ofNullable(urlRepository.findByShortUrlKey(shortUrl))
            .orElseThrow(() -> new ResourceNotFoundException("There is no link to " + shortUrl)));

        if(urlMapping.isPresent()) {
            if(urlMapping.get().getExpiryTime() != null && urlMapping.get().getExpiryTime().before(new Date())) {
                urlRepository.delete(urlMapping.get());
                throw new ResourceNotFoundException("Link Expired");
            }
        }

        return urlMapping.get().getMainUrl();
    }

    private String generateShortUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrlBuilder = new StringBuilder();

        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(characters.length());
            shortUrlBuilder.append(characters.charAt(randomIndex));
        }

        return shortUrlBuilder.toString();
    }

}
