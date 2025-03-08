package com.ahnis.journalai.common.util;

import com.ahnis.journalai.common.config.vectorstore.properties.VectorStoreProperties;
import io.milvus.client.MilvusClient;
import io.milvus.param.collection.DropCollectionParam;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "admin.vectorstore.clear-collection", havingValue = "true")
public final class CustomVectorStoreUtils {

    private final MilvusClient milvusClient;
    private final VectorStoreProperties vectorStoreProperties;

    @PostConstruct//todo enable this commented in dev for extra protection
    public void dropEntireCollection() {
        log.warn("Dropping entire vector store collection {} (Admin Task)", vectorStoreProperties.getCollectionName());
        milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withDatabaseName(vectorStoreProperties.getDatabaseName())
                        .withCollectionName(vectorStoreProperties.getCollectionName())
                        .build()
        );
        log.warn("Collection dropped successfully.");
    }
}



