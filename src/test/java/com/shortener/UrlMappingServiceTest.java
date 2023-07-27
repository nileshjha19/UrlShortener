package com.shortener;

import com.shortener.exception.ResourceAlreadyExistsException;
import com.shortener.exception.ResourceNotFoundException;
import com.shortener.model.UrlMapping;
import com.shortener.repository.UrlRepository;
import com.shortener.service.UrlConfigurationsService;
import com.shortener.service.UrlMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UrlMappingServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlConfigurationsService urlConfigurationsService;

    @InjectMocks
    private UrlMappingService urlMappingService;

    private final String BASE_URL = "https://tiney.com/";
    private final String SHORT_URL_KEY = "shorturlkey";
    private final String MAIN_URL = "https://www.example.com";
    private final Date FUTURE_EXPIRY = new Date(System.currentTimeMillis() + 1000000);
    private final Date PAST_EXPIRY = new Date(System.currentTimeMillis() - 1000000);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShortenUrlWithExistingMappingAndFutureExpiry() {
        UrlMapping existingMapping = new UrlMapping(SHORT_URL_KEY, MAIN_URL, FUTURE_EXPIRY);
        when(urlRepository.findByMainUrl(MAIN_URL)).thenReturn(existingMapping);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(existingMapping);

        String shortUrl = urlMappingService.shortenUrl(MAIN_URL, FUTURE_EXPIRY);

        assertEquals(BASE_URL + SHORT_URL_KEY, shortUrl);
        assertEquals(FUTURE_EXPIRY, existingMapping.getExpiryTime());
        verify(urlRepository, times(1)).findByMainUrl(MAIN_URL);
        verify(urlRepository, times(1)).save(existingMapping);
    }

    @Test
    public void testShortenUrlWithExistingMappingAndPastExpiry() {
        UrlMapping existingMapping = new UrlMapping(SHORT_URL_KEY, MAIN_URL, PAST_EXPIRY);
        when(urlRepository.findByMainUrl(MAIN_URL)).thenReturn(existingMapping);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(existingMapping);
        when(urlConfigurationsService.findByConfigKey(UrlMappingService.SHORT_URL_LENGTH)).thenReturn("6");
        String shortUrl = urlMappingService.shortenUrl(MAIN_URL, FUTURE_EXPIRY);

        assertNotEquals(BASE_URL + SHORT_URL_KEY, shortUrl);
        assertNotEquals(PAST_EXPIRY, existingMapping.getExpiryTime());
        verify(urlRepository, times(1)).findByMainUrl(MAIN_URL);
        verify(urlRepository, times(1)).save(existingMapping);
    }

    @Test
    public void testShortenUrlWithNewMapping() {
        when(urlRepository.findByMainUrl(MAIN_URL)).thenReturn(null);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(new UrlMapping(SHORT_URL_KEY, MAIN_URL, FUTURE_EXPIRY));
        when(urlConfigurationsService.findByConfigKey(UrlMappingService.SHORT_URL_LENGTH)).thenReturn("6");

        String shortUrl = urlMappingService.shortenUrl(MAIN_URL, FUTURE_EXPIRY);

        assertNotNull(shortUrl);
        assertTrue(shortUrl.startsWith(BASE_URL));
        verify(urlRepository, times(1)).findByMainUrl(MAIN_URL);
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
        verify(urlConfigurationsService, times(1)).findByConfigKey(UrlMappingService.SHORT_URL_LENGTH);
    }

    @Test
    public void testShortenCustomUrlWithExistingCustomUrlKey() {
        String customUrlKey = "customkey";
        when(urlRepository.findByShortUrlKey(customUrlKey)).thenReturn(new UrlMapping());

        assertThrows(ResourceAlreadyExistsException.class, () -> urlMappingService.shortenCustomUrl(MAIN_URL, customUrlKey, FUTURE_EXPIRY));
        verify(urlRepository, times(1)).findByShortUrlKey(customUrlKey);
    }

    @Test
    public void testShortenCustomUrlWithExistingMapping() {
        String customUrlKey = "customkey";
        UrlMapping existingMapping = new UrlMapping(SHORT_URL_KEY, MAIN_URL, PAST_EXPIRY);
        when(urlRepository.findByShortUrlKey(customUrlKey)).thenReturn(null);
        when(urlRepository.findByMainUrl(MAIN_URL)).thenReturn(existingMapping);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(existingMapping);

        String shortUrl = urlMappingService.shortenCustomUrl(MAIN_URL, customUrlKey, FUTURE_EXPIRY);

        assertEquals(BASE_URL + customUrlKey, shortUrl);
        assertEquals(FUTURE_EXPIRY, existingMapping.getExpiryTime());
        verify(urlRepository, times(1)).findByShortUrlKey(customUrlKey);
        verify(urlRepository, times(1)).findByMainUrl(MAIN_URL);
        verify(urlRepository, times(1)).save(existingMapping);
    }

    @Test
    public void testShortenCustomUrlWithNewMapping() {
        String customUrlKey = "customkey";
        when(urlRepository.findByShortUrlKey(customUrlKey)).thenReturn(null);
        when(urlRepository.findByMainUrl(MAIN_URL)).thenReturn(null);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(new UrlMapping(customUrlKey, MAIN_URL, FUTURE_EXPIRY));

        String shortUrl = urlMappingService.shortenCustomUrl(MAIN_URL, customUrlKey, FUTURE_EXPIRY);

        assertNotNull(shortUrl);
        assertTrue(shortUrl.startsWith(BASE_URL + customUrlKey));
        verify(urlRepository, times(1)).findByShortUrlKey(customUrlKey);
        verify(urlRepository, times(1)).findByMainUrl(MAIN_URL);
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    public void testGetOriginalUrlWithValidShortUrlAndFutureExpiry() {
        UrlMapping urlMapping = new UrlMapping(SHORT_URL_KEY, MAIN_URL, FUTURE_EXPIRY);
        when(urlRepository.findByShortUrlKey(SHORT_URL_KEY)).thenReturn(urlMapping);

        String originalUrl = urlMappingService.getOriginalUrl(SHORT_URL_KEY);

        assertEquals(MAIN_URL, originalUrl);
        verify(urlRepository, times(1)).findByShortUrlKey(SHORT_URL_KEY);
    }

    @Test
    public void testGetOriginalUrlWithValidShortUrlAndPastExpiry() {
        UrlMapping urlMapping = new UrlMapping(SHORT_URL_KEY, MAIN_URL, PAST_EXPIRY);
        when(urlRepository.findByShortUrlKey(SHORT_URL_KEY)).thenReturn(urlMapping);

        assertThrows(ResourceNotFoundException.class, () -> urlMappingService.getOriginalUrl(SHORT_URL_KEY));
        verify(urlRepository, times(1)).findByShortUrlKey(SHORT_URL_KEY);
    }

    @Test
    public void testGetOriginalUrlWithInvalidShortUrl() {
        when(urlRepository.findByShortUrlKey(SHORT_URL_KEY)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> urlMappingService.getOriginalUrl(SHORT_URL_KEY));
        verify(urlRepository, times(1)).findByShortUrlKey(SHORT_URL_KEY);
    }
}
