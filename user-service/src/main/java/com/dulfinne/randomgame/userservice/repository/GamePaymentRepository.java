package com.dulfinne.randomgame.userservice.repository;

import com.dulfinne.randomgame.userservice.entity.GamePayment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GamePaymentRepository extends ReactiveMongoRepository<GamePayment, String> {}
