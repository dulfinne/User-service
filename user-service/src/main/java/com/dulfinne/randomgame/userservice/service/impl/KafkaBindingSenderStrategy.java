package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.kafka.bind.BinderMessagingService;
import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;
import com.dulfinne.randomgame.userservice.service.TransactionSenderStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaBindingSenderStrategy implements TransactionSenderStrategy {

  private final BinderMessagingService binderMessagingService;

  @Override
  public void send(TransactionMessage message) {
    binderMessagingService.sendTransactionMessage(message);
  }
}
