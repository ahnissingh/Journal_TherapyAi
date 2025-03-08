package com.ahnis.journalai.common.config.vectorstore.properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "milvus.cloud")
public class MilvusCloudProperties {

    private String uri;
    private String token;
    private String username;
    private String password;

}
