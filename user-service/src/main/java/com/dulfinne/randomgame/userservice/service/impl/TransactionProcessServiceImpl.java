package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;
import com.dulfinne.randomgame.userservice.service.TransactionProcessService;
import com.dulfinne.randomgame.userservice.service.TransactionSenderStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TransactionProcessServiceImpl implements TransactionProcessService {
  private final List<TransactionSenderStrategy> strategies;
  private final AtomicInteger currentStrategy = new AtomicInteger(0);

  public void process(TransactionMessage message) {
    strategies.get(currentStrategy.get())
              .send(message);
  }

  public void moveToNextStrategy() {
    currentStrategy.updateAndGet(i -> (i + 1) % strategies.size());
  }
}
