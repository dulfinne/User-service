package com.dulfinne.randomgame.userservice.kafka.config;

import com.dulfinne.randomgame.userservice.exception.UnableParseMessageException;
import com.dulfinne.randomgame.userservice.kafka.entity.Payment;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
public class PaymentDeserializer implements Deserializer<Payment> {

  private ObjectMapper objectMapper;

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    objectMapper = new ObjectMapper();
  }

  @Override
  public Payment deserialize(String topic, byte[] data) {
    if (data == null || data.length == 0) return null;

    try {
      return objectMapper.readValue(data, Payment.class);
    } catch (IOException e) {
      throw new UnableParseMessageException(String.format(ExceptionKeys.TOPIC_PARSE_UNABLE, topic));
    }
  }

  @Override
  public void close() {}
}
