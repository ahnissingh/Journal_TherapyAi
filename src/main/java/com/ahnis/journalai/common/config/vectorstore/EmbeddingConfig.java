//package com.ahnis.journalai.common.config.vectorstore;
//
//import com.knuddels.jtokkit.api.EncodingType;
//import org.springframework.ai.embedding.BatchingStrategy;
//import org.springframework.ai.embedding.TokenCountBatchingStrategy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Configuration class for defining a custom batching strategy for token counting.
// * This class is designed to work with OpenAI's token limits and encoding requirements.
// * It provides a {@link BatchingStrategy} bean that ensures documents are batched
// * efficiently without exceeding OpenAI's maximum token limit.
// *
// * <p>The batching strategy uses the {@link TokenCountBatchingStrategy} implementation,
// * which groups documents based on their token counts and ensures that each batch
// * does not exceed a specified maximum token count. This is particularly useful
// * when working with embedding models or other AI models that have strict token limits.
// *
// * <p>This configuration is tailored for OpenAI's {@link EncodingType#CL100K_BASE} encoding
// * and a maximum token count of 8191, with a 10% reserve for overhead.
// *
// * @author Ahnis Singh
// * @version 1.0
// * @see TokenCountBatchingStrategy
// * @see EncodingType
// * @see BatchingStrategy
// */
//@Configuration
//public class EmbeddingConfig {
//
//    /**
//     * Defines a custom {@link BatchingStrategy} bean using the {@link TokenCountBatchingStrategy}
//     * implementation. The strategy is configured with parameters that align with OpenAI's
//     * tokenization requirements.
//     *
//     * <p>The batching strategy is configured as follows:
//     * <ul>
//     *     <li><strong>Encoding Type:</strong> {@link EncodingType#CL100K_BASE} (used by OpenAI models).</li>
//     *     <li><strong>Max Token Count:</strong> 8191 (OpenAI's maximum context window size).</li>
//     *     <li><strong>Reserve Percentage:</strong> 0.1 (10% buffer for overhead).</li>
//     * </ul>
//     *
//     * @return A {@link BatchingStrategy} bean configured for OpenAI's token limits and encoding requirements.
//     */
//    @Bean
//    public BatchingStrategy CustombatchingStrategyForOpenAi() {
//        return new TokenCountBatchingStrategy(
//                EncodingType.CL100K_BASE,
//                8191,
//                0.1
//        );
//    }
//}
