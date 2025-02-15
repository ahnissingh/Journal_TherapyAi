package com.ahnis.journalai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "ahnis.aiapp")
public class VectorStoreProperties {
    private String vectorStorePath;
    private List<Resource> documentsToLoad;
}
