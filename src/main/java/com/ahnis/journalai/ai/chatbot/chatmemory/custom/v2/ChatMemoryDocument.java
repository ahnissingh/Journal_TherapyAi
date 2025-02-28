//package com.ahnis.journalai.chatbot.chatmemory.custom.v2;
//
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.time.Instant;
//
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Document(collection = "chat_memory_v2")
//public class ChatMemoryDocument {
//    // Getters and Setters
//    @Id
//    private String id;
//    @Indexed
//    @Field("user_id")
//    private String userId;
//    @Field("chat_id")
//    private String chatId;
//    @Indexed
//    @Field("conversation_id")
//    private String conversationId;
//    @Field("message_type")
//    private String messageType;
//    @Field("message_text")
//    private String messageText;
//    @Field("timestamp")
//    private Instant timestamp;
//
//    // Constructor
//
//
//}
