package com.example.doctor_service.Services;

import com.example.doctor_service.Controllers.DoctorController;
import com.example.doctor_service.Kafka.Events.*;
import com.example.doctor_service.Model.Doctor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumer {

    final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "doctor-topic", groupId = "${spring.kafka.consumer.group-id}" )
    public void listen(String message) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(message);
        String eventType = root.get("eventType").asText();
        JsonNode payload = root.get("payload");

        logger.info("Received message: {} {}", eventType, payload);
    }

}
