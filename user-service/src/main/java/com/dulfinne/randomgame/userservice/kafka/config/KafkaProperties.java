package com.dulfinne.randomgame.userservice.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka")
public record KafkaProperties(
    String bootstrapServers,
    Integer consumersNumber,
    Topics topics,
    Groups groups,
    ErrorHandler errorHandler,
    String dltPostfix
) {

  public record Topics(String gamePayments) {}

  public record Groups(String gamePayments) {}

  public record ErrorHandler(
    Integer minInterval,
    Integer maxInterval,
    Double multiplier,
    Integer maxAttempts
  ) {}
}
