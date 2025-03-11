package com.ahnis.journalai.common.util;

import com.ahnis.journalai.common.config.vectorstore.properties.VectorStoreProperties;
import io.milvus.client.MilvusClient;
import io.milvus.param.collection.DropCollectionParam;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Utility class for performing administrative tasks on the vector store collection.
 * <p>
 * This class provides functionality to drop the entire vector store collection, which is typically used
 * for administrative or maintenance purposes. The operation is conditionally enabled based on the
 * `admin.vectorstore.clear-collection` property in the application configuration.
 * </p>
 * <p>
 * <b>Warning:</b> Dropping a collection is a destructive operation and will permanently delete all data
 * in the specified collection. Use this utility with caution.
 * </p>
 *
 * @author Ahnis Singh Aneja
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "admin.vectorstore.clear-collection", havingValue = "true")
public final class CustomVectorStoreUtils {

    private final MilvusClient milvusClient;
    private final VectorStoreProperties vectorStoreProperties;

    /**
     * Drops the entire vector store collection.
     * <p>
     * This method is annotated with `@PostConstruct`, meaning it will automatically execute after the
     * bean is initialized, provided the `admin.vectorstore.clear-collection` property is set to `true`.
     * It logs a warning before and after the operation to ensure the administrator is aware of the action.
     * </p>
     * <p>
     * <b>Note:</b> The `@PostConstruct` annotation is currently commented out to prevent accidental execution.
     * Uncomment it only when explicitly required.
     * </p>
     */
    @PostConstruct
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
