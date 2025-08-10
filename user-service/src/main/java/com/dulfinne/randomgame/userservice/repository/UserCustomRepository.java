package com.dulfinne.randomgame.userservice.repository;

import com.dulfinne.randomgame.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class UserCustomRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  public Flux<User> findUsersWithPagination(Integer offset, Integer limit) {
    Query query = new Query().skip(offset).limit(limit);
    return mongoTemplate.find(query, User.class);
  }
}
