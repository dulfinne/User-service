package com.dulfinne.randomgame.userservice.kafka.bind;

import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinderMessagingService {

  private final StreamBridge streamBridge;

  public void sendTransactionMessage(TransactionMessage transaction) {
    log.info("Sending transaction using BINDER: {}", transaction);
    streamBridge.send("transaction-topic", transaction);
  }
}
