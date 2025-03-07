package com.ahnis.journalai.common.util;

import io.milvus.client.MilvusClient;
import io.milvus.param.collection.DropCollectionParam;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "admin.vectorstore.clear-collection", havingValue = "true")
public final class CustomVectorStoreUtils {

    private final MilvusClient milvusClient;
    private final MilvusVectorStoreProperties milvusVectorStoreProperties;

    @PostConstruct//todo enable this commented in dev for extra protection
    public void dropEntireCollection() {
        log.warn("Dropping entire vector store collection {} (Admin Task)", milvusVectorStoreProperties.getCollectionName());
        milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName("vector_store")
                        .build()
        );
        log.warn("Collection dropped successfully.");
    }
}



