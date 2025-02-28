package com.ahnis.journalai.common.util;

import io.milvus.client.MilvusClient;
import io.milvus.param.collection.DropCollectionParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomVectorStoreUtils {
    private final MilvusClient milvusClient;
    private final MilvusVectorStoreProperties milvusVectorStoreProperties;

    public void clearAll() {
        log.warn("Dropping entire vector store collection");
        milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withDatabaseName(milvusVectorStoreProperties.getDatabaseName())
                        .withCollectionName(milvusVectorStoreProperties.getCollectionName())
                        .build()
        );
        log.warn("Collection dropped successfully.");
    }
}

