package com.dulfinne.randomgame.userservice.kafka.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;
  private final KafkaOperations<Object, Object> operations;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
    props.put(
        ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        CooperativeStickyAssignor.class.getName());

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, PaymentDeserializer.class);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString());
    return props;
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(kafkaProperties.consumersNumber());
    factory.setCommonErrorHandler(errorHandler());
    return factory;
  }

  @Bean
  public DefaultErrorHandler errorHandler() {
    ExponentialBackOff backOff = new ExponentialBackOff();
    backOff.setInitialInterval(kafkaProperties.errorHandler().minInterval());
    backOff.setMaxInterval(kafkaProperties.errorHandler().maxInterval());
    backOff.setMultiplier(kafkaProperties.errorHandler().multiplier());
    backOff.setMaxAttempts(kafkaProperties.errorHandler().maxAttempts());

    return getDefaultErrorHandler(backOff);
  }

  private DefaultErrorHandler getDefaultErrorHandler(ExponentialBackOff backOff) {
    var dltRecoverer =
        new DeadLetterPublishingRecoverer(
            operations,
            (cr, e) -> new TopicPartition(cr.topic() + kafkaProperties.dltPostfix(), 0));
    dltRecoverer.setLogRecoveryRecord(true);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(dltRecoverer, backOff);

    errorHandler.setRetryListeners(
        (message, ex, deliveryAttempt) ->
            log.warn(
                "Retry attempt #{} for record with key: {}. Exception: {}",
                deliveryAttempt,
                message.key(),
                ex.toString()));
    return errorHandler;
  }
}
