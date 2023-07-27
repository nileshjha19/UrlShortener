package com.shortener.repository;

import com.shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, String> {

    UrlMapping findByMainUrl(String url);

    UrlMapping findByShortUrlKey(String shortUrl);
}
