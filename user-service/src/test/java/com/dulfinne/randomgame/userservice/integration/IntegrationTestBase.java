package com.dulfinne.randomgame.userservice.integration;

import com.dulfinne.randomgame.userservice.util.HeaderConstants;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

  @LocalServerPort private int port;

  protected RequestSpecification withAuth(String username) {
    return given()
        .header(HeaderConstants.USERNAME_HEADER, username)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .port(port);
  }

  @Container
  public static final MongoDBContainer container =
      new MongoDBContainer(DockerImageName.parse("mongo:8.0.4"));

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", container::getConnectionString);
  }
}
