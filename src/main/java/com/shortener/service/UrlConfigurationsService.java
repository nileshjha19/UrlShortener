package com.shortener.service;

import com.shortener.model.UrlConfigurations;
import com.shortener.repository.ConfigurationsRepository;
import com.shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlConfigurationsService {
    ConfigurationsRepository configurationsRepository;

    @Autowired
    public void UrlConfigurationsService(ConfigurationsRepository configurationsRepository) {
        this.configurationsRepository = configurationsRepository;
    }

    public String findByConfigKey(String configKey) {
        UrlConfigurations urlConfigurations = configurationsRepository.findByConfigKey(configKey);
        if(urlConfigurations != null)
            return urlConfigurations.getConfigValue();
        return null;
    }


}
