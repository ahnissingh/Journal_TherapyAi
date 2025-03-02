package com.ahnis.journalai.common.util;

import io.milvus.client.MilvusClient;
import io.milvus.param.collection.DropCollectionParam;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
//todo decide whether to use spring bean or static method
//
//@Component
//@Lazy
//@Slf4j
//@RequiredArgsConstructor
//@ConditionalOnProperty(name = "admin.vectorstore.clear-collection", havingValue = "true")
//public final class CustomVectorStoreUtils {
//
//    private final MilvusClient milvusClient;
//    private final MilvusVectorStoreProperties milvusVectorStoreProperties;
//
////    @PostConstruct
//    public void dropEntireCollection() {
//        if (!"local".equals(System.getProperty("spring.profiles.active"))) {
//            throw new IllegalStateException("This method can only be called in local profile.");
//        }
//        log.warn("Dropping entire vector store collection {} (Admin Task)", milvusVectorStoreProperties.getCollectionName());
//        milvusClient.dropCollection(
//                DropCollectionParam.newBuilder()
//                        .withDatabaseName(milvusVectorStoreProperties.getDatabaseName())
//                        .withCollectionName(milvusVectorStoreProperties.getCollectionName())
//                        .build()
//        );
//        log.warn("Collection dropped successfully.");
//    }
//}

public final class CustomVectorStoreUtils {
    private CustomVectorStoreUtils() {
        throw new UnsupportedOperationException("Cannot initialise Util class");
    }

    public static void dropCollection(MilvusClient client, String database, String collection) {
        client.dropCollection(
                DropCollectionParam.newBuilder()
                        .withDatabaseName(database)
                        .withCollectionName(collection)
                        .build()
        );
    }
}

