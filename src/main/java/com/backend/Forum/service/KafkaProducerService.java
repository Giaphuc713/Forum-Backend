// package com.backend.Forum.service;

// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;

// @Service
// @Transactional
// public class KafkaProducerService {

// private static final String TOPIC = "notifications";
// private final KafkaTemplate<String, String> kafkaTemplate;

// public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
// this.kafkaTemplate = kafkaTemplate;
// }

// public void sendMessage(String message) {
// kafkaTemplate.send(TOPIC, message);
// System.out.println("Message sent: " + message);
// }
// }