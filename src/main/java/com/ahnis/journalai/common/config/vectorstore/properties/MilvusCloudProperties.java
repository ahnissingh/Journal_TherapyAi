package com.ahnis.journalai.common.config.vectorstore.properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "milvus.cloud") // Prefix for environment variables
public class MilvusCloudProperties {

    private String uri; // MILVUS_CLOUD_URI
    private String token; // MILVUS_CLOUD_TOKEN
    private String username; // MILVUS_CLOUD_USERNAME
    private String password; // MILVUS_CLOUD_PASSWORD

}
