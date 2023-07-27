package com.employee.springbootbackend.repository;

import java.util.Date;

import com.employee.springbootbackend.model.Employee;
import com.employee.springbootbackend.model.UrlMapping;
import com.employee.springbootbackend.service.UrlMappingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, String> {

    UrlMapping findByMainUrl(String url);

    UrlMapping findByShortUrlKey(String shortUrl);
}
