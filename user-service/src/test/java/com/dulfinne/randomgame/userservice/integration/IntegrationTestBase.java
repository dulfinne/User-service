package com.dulfinne.randomgame.userservice.integration;

import com.dulfinne.randomgame.userservice.util.CommonConstants;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

  @LocalServerPort
  private int port;

  protected RequestSpecification withAuth(String username) {
    return given()
        .header(CommonConstants.USERNAME_HEADER, username)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .port(port);
  }

  @Container
  public static final MongoDBContainer mongoContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:8.0.4"));

  @Container
  public static final KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("apache/kafka:3.9.1"));


  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URL", mongoContainer::getConnectionString);
    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
  }
}
