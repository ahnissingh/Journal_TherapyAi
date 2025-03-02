package com.ahnis.journalai.ai.embedding;

import com.ahnis.journalai.journal.entity.Journal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalEmbeddingService {
    private final VectorStore vectorStore;

    @Async
    public void saveJournalEmbeddings(Journal journal) {
        try {
            var document = new Document(
                    journal.getUserId(),
                    journal.getContent(),
                    Map.of(
                            "title", journal.getTitle(),
                            "userId", journal.getUserId(),
                            "createdAt", journal.getCreatedAt()
                    )
            );
            var textSplitter = new TokenTextSplitter();
            var splitDocuments = textSplitter.apply(List.of(document));
            log.info("Adding document {} ", document);
            vectorStore.add(splitDocuments);
            log.info("Added document {} ", document);

        } catch (Exception e) {
            log.error("Failed to save journal embedding {} error {} ", journal, e);
        }
    }
}
