package com.dulfinne.randomgame.userservice.integration;

import com.dulfinne.randomgame.userservice.entity.GamePayment;
import com.dulfinne.randomgame.userservice.entity.User;
import com.dulfinne.randomgame.userservice.grpc.GrpcClientService;
import com.dulfinne.randomgame.userservice.grpc.TransactionProto;
import com.dulfinne.randomgame.userservice.kafka.config.KafkaProperties;
import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import com.dulfinne.randomgame.userservice.repository.GamePaymentRepository;
import com.dulfinne.randomgame.userservice.repository.UserRepository;
import com.dulfinne.randomgame.userservice.util.UserTestData;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Durations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;

@RequiredArgsConstructor
public class KafkaIT extends IntegrationTestBase {

  private final KafkaTemplate<Object, Object> kafkaTemplate;

  private final KafkaProperties kafkaProperties;

  private final UserRepository userRepository;

  private final GamePaymentRepository paymentRepository;

  @MockitoBean
  private GrpcClientService grpcClientService;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll().block();
    paymentRepository.deleteAll().block();
  }

  @Nested
  class ReceivePayment {

    @Test
    void givenPaymentMessage_whenFlagIsPositive_thenUserBalanceIncremented() throws ExecutionException, InterruptedException {
      Mockito.when(grpcClientService.saveTransaction(any(TransactionProto.Transaction.class)))
             .thenReturn(true);

      userRepository.save(UserTestData.getFirstUser().build()).block();

      Payment payment = UserTestData.getPayment().positiveFlag(Boolean.TRUE).build();
      BigDecimal expectedBalance = UserTestData.FIRST_BALANCE.add(payment.amount());

      kafkaTemplate.send(
                       kafkaProperties.topics()
                                      .gamePayments(), UserTestData.FIRST_USERNAME, payment)
                   .get();

      await()
          .atMost(Durations.FIVE_SECONDS)
          .untilAsserted(
              () -> {
                Mono<User> userMono = userRepository.findByUsername(UserTestData.FIRST_USERNAME);
                Mono<GamePayment> paymentMono = paymentRepository.findById(UserTestData.FIRST_ID);

                StepVerifier.create(Mono.zip(userMono, paymentMono))
                    .assertNext(
                        tuple -> {
                          User user = tuple.getT1();
                          GamePayment paymentRecord = tuple.getT2();
                          assertThat(user.getBalance()).isEqualTo(expectedBalance);
                          assertThat(paymentRecord).isNotNull();
                        })
                    .expectComplete()
                    .verify();
              });
    }

    @Test
    void givenPaymentMessage_whenFlagIsNegative_thenUserBalanceDecremented() throws ExecutionException, InterruptedException {
      Mockito.when(grpcClientService.saveTransaction(any(TransactionProto.Transaction.class)))
             .thenReturn(true);

      userRepository.save(UserTestData.getFirstUser().build()).block();

      Payment payment = UserTestData.getPayment().positiveFlag(Boolean.FALSE).build();
      BigDecimal expectedBalance = UserTestData.FIRST_BALANCE.subtract(payment.amount());

      kafkaTemplate.send(
                       kafkaProperties.topics()
                                      .gamePayments(), UserTestData.FIRST_USERNAME, payment)
                   .get();

      await()
          .atMost(Durations.FIVE_SECONDS)
          .untilAsserted(
              () -> {
                Mono<User> userMono = userRepository.findByUsername(UserTestData.FIRST_USERNAME);
                Mono<GamePayment> paymentMono = paymentRepository.findById(UserTestData.FIRST_ID);

                StepVerifier.create(Mono.zip(userMono, paymentMono))
                    .assertNext(
                        tuple -> {
                          User user = tuple.getT1();
                          GamePayment paymentRecord = tuple.getT2();
                          assertThat(user.getBalance()).isEqualTo(expectedBalance);
                          assertThat(paymentRecord).isNotNull();
                        })
                    .expectComplete()
                    .verify();
              });
    }

    @Test
    void givenDuplicatePayment_whenConsumed_thenBalanceIsNotModified() throws ExecutionException, InterruptedException {
      paymentRepository.save(new GamePayment(UserTestData.FIRST_ID)).block();
      userRepository.save(UserTestData.getFirstUser().build()).block();

      Payment payment = UserTestData.getPayment().positiveFlag(Boolean.FALSE).build();
      BigDecimal expectedBalance = UserTestData.FIRST_BALANCE;

      kafkaTemplate.send(
                       kafkaProperties.topics()
                                      .gamePayments(), UserTestData.FIRST_USERNAME, payment)
                   .get();

      await()
          .atMost(Durations.FIVE_SECONDS)
          .untilAsserted(
              () -> {
                Mono<User> userMono = userRepository.findByUsername(UserTestData.FIRST_USERNAME);
                StepVerifier.create(Mono.from(userMono))
                    .assertNext(user -> assertThat(user.getBalance()).isEqualTo(expectedBalance))
                    .expectComplete()
                    .verify();
              });
    }
  }
}
