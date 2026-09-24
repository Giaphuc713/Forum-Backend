// package com.backend.Forum.controller;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.backend.Forum.service.KafkaProducerService;

// @RestController
// public class KafkaController {

// private final KafkaProducerService producerService;

// public KafkaController(KafkaProducerService producerService) {
// this.producerService = producerService;
// }

// @GetMapping("/kafka/send")
// public String sendMessage(@RequestParam String message) {
// producerService.sendMessage(message);
// return "Message sent successfully!";
// }
// }
