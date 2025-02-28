package com.ahnis.journalai.ai.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalAnalysisService {
    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    @Async
    public CompletableFuture<MoodReport> analyzeUserMood2(String userId) {
        // Step 1: Retrieve documents filtered by userId and date range
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Mood")
                        .topK(5)//userId == 'userId'
                        .filterExpression("userId == '" + userId + "'")
                        .build()
        );

        // Step 2: Extract the text content from the documents
        List<String> contentList = documents.stream()
                .map(Document::getText)
                .toList();

        // Step 3: Combine the content into a single string for the prompt
        var combinedContent = String.join("\n", contentList);

        // Step 4: Create a prompt to analyze the mood
        String promptTemplate = """
                Analyze the mood of the following journal entries and provide a summary.
                Include key emotions (as percentages), contextual insights, and recommendations.
                Your response should be in JSON format.
                The data structure for the JSON should match this Java class: %s
                Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
                Entries:
                {entries}
                """;

        String format = new BeanOutputConverter<>(MoodReport.class).getFormat();
        String promptText = String.format(promptTemplate, MoodReport.class.getName()) + "\n" + format;

        // Step 5: Send the prompt to the language model (e.g., OpenAI GPT)
        var response = chatModel.call(new Prompt(promptText));

        // Step 6: Parse the response into a MoodReport object
        BeanOutputConverter<MoodReport> outputConverter = new BeanOutputConverter<>(MoodReport.class);
        MoodReport moodReport = outputConverter.convert(response.getResult().getOutput().getContent());

        // Step 7: Return the report as a CompletableFuture
        return CompletableFuture.completedFuture(moodReport);
    }
}
