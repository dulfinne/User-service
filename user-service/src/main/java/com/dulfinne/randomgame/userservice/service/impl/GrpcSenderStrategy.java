package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.grpc.GrpcClientService;
import com.dulfinne.randomgame.userservice.grpc.TransactionProto;
import com.dulfinne.randomgame.userservice.kafka.entity.TransactionMessage;
import com.dulfinne.randomgame.userservice.service.TransactionSenderStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrpcSenderStrategy implements TransactionSenderStrategy {

  private final GrpcClientService grpcClient;

  @Override
  public void send(TransactionMessage message) {
    TransactionProto.Transaction transaction = TransactionProto.Transaction.newBuilder()
                                                                           .setUsername(message.username())
                                                                           .setAmount(message.amount()
                                                                                             .toString())
                                                                           .setType(message.type()
                                                                                           .toString())
                                                                           .build();
    log.info("Sending transaction using GRPC: {}", transaction);
    grpcClient.saveTransaction(transaction);
  }
}
