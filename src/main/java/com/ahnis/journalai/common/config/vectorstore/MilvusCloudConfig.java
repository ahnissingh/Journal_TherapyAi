package com.ahnis.journalai.common.config.vectorstore;

import com.ahnis.journalai.common.config.vectorstore.properties.MilvusCloudProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusCloudConfig {
    @Bean
    MilvusServiceClient milvusClient(MilvusCloudProperties cloudProperties) {
        return new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withUri(cloudProperties.getUri())
                        .withToken(cloudProperties.getToken())
                        .withAuthorization(cloudProperties.getUsername(), cloudProperties.getPassword())
                        .build()
        );
    }
}
