package com.ahnis.journalai.common.config.vectorstore.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "vector.store")
public class VectorStoreProperties {

    private String databaseName; // VECTOR_STORE_DATABASE_NAME
    private String collectionName; // VECTOR_STORE_COLLECTION_NAME
    private String metricType; // VECTOR_STORE_METRIC_TYPE
    private String indexType; // VECTOR_STORE_INDEX_TYPE
    private int embeddingDimension; // VECTOR_STORE_EMBEDDING_DIMENSION
    private boolean initializeSchema; // VECTOR_STORE_INITIALIZE_SCHEMA

}
