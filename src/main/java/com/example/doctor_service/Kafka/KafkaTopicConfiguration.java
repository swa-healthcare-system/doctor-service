package com.example.doctor_service.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {
    private final Logger logger = LoggerFactory.getLogger(KafkaTopicConfiguration.class);

//    @Value("${spring.kafka.topics.doctor}")
//    private String doctorTopic;
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        return new KafkaAdmin(configs);
//    }
//
//    @Bean
//    public NewTopic doctor_topic() {
//        logger.info("Creating topic for doctor service");
//        return TopicBuilder
//                .name(doctorTopic)
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
}
