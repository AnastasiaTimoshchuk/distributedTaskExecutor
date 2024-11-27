package com.anastasiat.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {

    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset;
    private String keyDeserializer;
    private String valueDeserializer;
}