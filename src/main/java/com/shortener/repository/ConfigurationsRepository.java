package com.shortener.repository;

import com.shortener.model.UrlConfigurations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationsRepository extends JpaRepository<UrlConfigurations, Long> {
    UrlConfigurations findByConfigKey(String configKey);
}
