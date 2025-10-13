package com.dulfinne.randomgame.userservice.service;

import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;

public interface TransactionSenderStrategy {
  void send(TransactionMessage message);
}