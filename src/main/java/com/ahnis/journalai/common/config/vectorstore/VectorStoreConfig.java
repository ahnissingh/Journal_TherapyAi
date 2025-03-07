package com.ahnis.journalai.common.config.vectorstore;


import com.ahnis.journalai.common.config.vectorstore.properties.VectorStoreProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(MilvusServiceClient milvusServiceClient,
                                   EmbeddingModel embeddingModel,
                                   VectorStoreProperties vectorStoreProperties
    ) {
        return MilvusVectorStore
                .builder(milvusServiceClient, embeddingModel)
                .databaseName(vectorStoreProperties.getDatabaseName())
                .collectionName(vectorStoreProperties.getCollectionName())
                .metricType(MetricType.valueOf(vectorStoreProperties.getMetricType()))
                .indexType(IndexType.valueOf(vectorStoreProperties.getIndexType()))
                .embeddingDimension(vectorStoreProperties.getEmbeddingDimension())
                .initializeSchema(vectorStoreProperties.isInitializeSchema())
                .build();
    }
}
