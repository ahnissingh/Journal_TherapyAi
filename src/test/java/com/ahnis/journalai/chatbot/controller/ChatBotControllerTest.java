package com.ahnis.journalai.chatbot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatBotControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @SuppressWarnings("StringTemplateMigration")
    @Test
    public void testSendMessageToChat() {


    }
}
