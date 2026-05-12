package com.dulfinne.randomgame.userservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "game_payment")
public record GamePayment(
  @Id
  String gameId
) {
}
