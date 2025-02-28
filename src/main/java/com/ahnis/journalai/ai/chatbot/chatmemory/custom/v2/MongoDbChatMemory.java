//package com.ahnis.journalai.chatbot.chatmemory.custom.v2;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.messages.Message;
//import org.springframework.ai.chat.messages.AssistantMessage;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.data.mongodb.core.index.CompoundIndex;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.Comparator;
//import java.util.List;
//
//@Document(collection = "chat_memory")
//@CompoundIndex(name = "conv_id_timestamp_idx", def = "{'conversationId': 1, 'timestamp': 1}")
//@Component
//public class MongoDbChatMemory implements ChatMemory {
//
//    private final ChatMemoryRepository chatMemoryRepository;
//
//    public MongoDbChatMemory(ChatMemoryRepository chatMemoryRepository) {
//        this.chatMemoryRepository = chatMemoryRepository;
//    }
//
//    @Override
//    public void add(String conversationId, List<Message> messages) {
//        String[] parts = conversationId.split(":");
//        String userId = parts[0];
//        String chatId = parts[1];
//
//        messages.forEach(message -> {
//            var doc = ChatMemoryDocument.builder()
//                    .userId(userId)
//                    .chatId(chatId)
//                    .conversationId(conversationId)
//                    .messageType(message.getMessageType().name())
//                    .messageText(message.getText())
//                    .timestamp(Instant.now())
//                    .build();
//
//            chatMemoryRepository.save(doc); // Save using repository
//        });
//    }
//
//    @Override
//    public List<Message> get(String conversationId, int lastN) {
//        // Fetch messages sorted by timestamp in descending order
//        List<ChatMemoryDocument> docs = chatMemoryRepository.findByConversationIdOrderByTimestampDesc(conversationId);
//
//        // Limit the results to the last N messages and restore chronological order
//        return docs.stream()
//                .limit(lastN)
//                .sorted(Comparator.comparing(ChatMemoryDocument::getTimestamp)) // Restore chronological order
//                .map(this::createMessage)
//                .toList();
//    }
//
//    @Override
//    public void clear(String conversationId) {
//        // Delete all messages for the conversationId
//        chatMemoryRepository.deleteByConversationId(conversationId);
//    }
//
//    private Message createMessage(ChatMemoryDocument doc) {
//        return switch (doc.getMessageType()) {
//            case "USER" -> new UserMessage(doc.getMessageText());
//            case "ASSISTANT" -> new AssistantMessage(doc.getMessageText());
//            default -> throw new IllegalArgumentException("Unknown message type: " + doc.getMessageType());
//        };
//    }
//}
