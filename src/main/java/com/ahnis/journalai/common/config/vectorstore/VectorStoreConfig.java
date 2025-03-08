package com.ahnis.journalai.common.config.vectorstore;


import com.ahnis.journalai.common.config.vectorstore.properties.VectorStoreProperties;
import com.knuddels.jtokkit.api.EncodingType;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class VectorStoreConfig {
    @Bean
    public BatchingStrategy CustomBatchingStrategyForOpenAi() {
        return new TokenCountBatchingStrategy(
                EncodingType.CL100K_BASE,
                8191,
                0.1
        );
    }

    @Bean
    public VectorStore vectorStore(MilvusServiceClient milvusServiceClient,
                                   EmbeddingModel embeddingModel,
                                   VectorStoreProperties vectorStoreProperties,
                                   BatchingStrategy batchingStrategy
    ) {
        return MilvusVectorStore
                .builder(milvusServiceClient, embeddingModel)
                .databaseName(vectorStoreProperties.getDatabaseName())
                .collectionName(vectorStoreProperties.getCollectionName())
                .metricType(MetricType.valueOf(vectorStoreProperties.getMetricType()))
                .indexType(IndexType.valueOf(vectorStoreProperties.getIndexType()))
                .batchingStrategy(batchingStrategy)
                .embeddingDimension(vectorStoreProperties.getEmbeddingDimension())
                .initializeSchema(vectorStoreProperties.isInitializeSchema())
                .build();
    }
}
