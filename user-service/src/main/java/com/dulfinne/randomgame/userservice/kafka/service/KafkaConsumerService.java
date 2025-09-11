package com.dulfinne.randomgame.userservice.kafka.service;

import com.dulfinne.randomgame.userservice.exception.KafkaProcessingException;
import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import com.dulfinne.randomgame.userservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
  private final PaymentService paymentService;

  @KafkaListener(
      topics = "${spring.kafka.topics.game-payments}",
      groupId = "${spring.kafka.groups.game-payments}",
      containerFactory = "kafkaListenerContainerFactory")
  public Void listen(Payment request) {
    return paymentService
        .processPayment(request)
        .doOnError(
            e -> {
              throw new KafkaProcessingException(e.getMessage());
            })
        .block();
  }
}
