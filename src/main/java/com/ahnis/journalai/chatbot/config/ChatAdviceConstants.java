package com.ahnis.journalai.chatbot.config;

/**
 * This class contains static final constants for prompts used by the chatbot advisors.
 * These prompts are specifically designed for the {@link org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor}
 * and {@link org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor}.
 * <p>
 * <b>Important:</b> <b>Do not change the variable names of the constants, as they are
 * internally referenced by the framework. Modifying the variable names may cause
 * runtime errors or unexpected behavior.</b>
 * </p>
 *
 * <p>
 * Written by: Ahnis Singh Aneja
 * </p>
 */
public final class ChatAdviceConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException if an attempt is made to instantiate this class.
     */
    private ChatAdviceConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Prompt template for the {@link org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor}.
     * <p>
     * This prompt is used to generate responses based on the context provided by the user's journal entries.
     * It emphasizes providing personalized, empathetic, actionable, and concise responses. If the context
     * does not contain enough information to answer the user's question, the advisor will politely inform
     * the user.
     * </p>
     * <p>
     * <b>Template Variables:</b>
     * <ul>
     *   <li>{@code {question_answer_context}}: The context extracted from the user's journal entries.</li>
     * </ul>
     * </p>
     */
    public static final String QUESTION_ANSWER_ADVISOR_PROMPT =
            """
                    Context information is below, surrounded by ---------------------

                    ---------------------
                    {question_answer_context}
                    ---------------------
                    Given the context of the user's journal entries above, provide a thoughtful and empathetic response.
                    Focus on the emotions, experiences, and insights shared in the journals.
                    If the user's question is related to their mood or experiences, analyze the context and provide a summary or advice based on the entries.
                    If the question cannot be answered using the context, politely inform the user that you don't have enough information to respond.

                    Your response should be:
                    - Personalized: Address the user directly and reference specific details from their journals or their stored preferences
                    - Empathetic: Acknowledge the user's emotions and experiences.
                    - Actionable: Provide suggestions or insights that the user can act upon.
                    - Concise: Keep the response clear and to the point.

                    Do not include any explanations or notes about the process—only provide the response.
                    """;

    /**
     * Prompt template for the {@link org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor}.
     * <p>
     * This prompt is used to generate responses based on the long-term conversation memory stored in the
     * vector store. It leverages the user's past interactions and preferences to provide accurate and
     * tailored responses.
     * </p>
     * <p>
     * <b>Template Variables:</b>
     * <ul>
     *   <li>{@code {long_term_memory}}: The long-term conversation memory retrieved from the vector store.</li>
     * </ul>
     * </p>
     */
    public static final String VECTOR_CHAT_ADVISOR_PROMPT =
            """
                    Use the long term conversation memory from the LONG_TERM_MEMORY section to provide accurate answers.
                    ---------------------
                    LONG_TERM_MEMORY:
                    {long_term_memory}
                    ---------------------
                    Additionally, consider the user's preferences and past interactions to tailor the response more effectively.
                    """;
}
