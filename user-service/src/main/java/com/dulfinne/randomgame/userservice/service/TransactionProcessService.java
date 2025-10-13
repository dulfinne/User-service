package com.dulfinne.randomgame.userservice.service;

import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;

public interface TransactionProcessService {
  void process(TransactionMessage message);

  void moveToNextStrategy();
}
