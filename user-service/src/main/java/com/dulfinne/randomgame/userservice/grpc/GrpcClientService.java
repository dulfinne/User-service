package com.dulfinne.randomgame.userservice.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

  @GrpcClient("transactionService")
  private TransactionServiceGrpc.TransactionServiceBlockingStub greeterBlockingStub;

  public Boolean saveTransaction(TransactionProto.Transaction transaction) {
    TransactionProto.SavedReply reply = greeterBlockingStub.saveTransaction(transaction);
    return reply.getIsSaved();
  }
}