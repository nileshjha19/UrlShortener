package com.shortener.repository;

import javax.transaction.Transactional;

import com.shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UrlRepository extends JpaRepository<UrlMapping, String> {

    UrlMapping findByMainUrl(String url);

    UrlMapping findByShortUrlKey(String shortUrl);
}
