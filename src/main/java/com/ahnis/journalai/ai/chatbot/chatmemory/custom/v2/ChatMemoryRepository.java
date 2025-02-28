//package com.ahnis.journalai.chatbot.chatmemory.custom.v2;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ChatMemoryRepository extends MongoRepository<ChatMemoryDocument, String> {
//
//    // Find all messages for a conversationId, sorted by timestamp in descending order
//    List<ChatMemoryDocument> findByConversationIdOrderByTimestampDesc(String conversationId);
//    // Delete all messages for a conversationId
//    void deleteByConversationId(String conversationId);
//}
