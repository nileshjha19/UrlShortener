package com.shortener.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "url_mapping", indexes = {@Index(name = "IDX_URL", columnList = "main_url"),
    @Index(name = "IDX_EXPIRY_TIME", columnList = "expiry_time")
})
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "main_url")
    private String mainUrl;

    @Column(name = "short_url_key")
    private String shortUrlKey;

    @Column(name = "expiry_time")
    private Date expiryTime;

    public String getMainUrl() {
        return mainUrl;
    }

    public String getShortUrlKey() {
        return shortUrlKey;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
