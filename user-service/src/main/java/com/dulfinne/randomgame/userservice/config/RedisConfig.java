package com.dulfinne.randomgame.userservice.config;

import com.dulfinne.randomgame.userservice.util.CommonConstants;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

  @Bean
  public RedisSerializationContext.SerializationPair<String> keySerializer() {
    return RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
  }

  @Bean
  public RedisSerializationContext.SerializationPair<Object> valueSerializer() {
    return RedisSerializationContext.SerializationPair.fromSerializer(
        new GenericJackson2JsonRedisSerializer());
  }

  @Bean
  public RedisCacheConfiguration defaultCacheConfig(
      RedisSerializationContext.SerializationPair<String> keySerializer,
      RedisSerializationContext.SerializationPair<Object> valueSerializer) {
    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(keySerializer)
        .serializeValuesWith(valueSerializer);
  }

  @Bean
  public RedisCacheManager redisCacheManager(
      RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration defaultCacheConfig) {
    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfig)
        .withCacheConfiguration(
            CommonConstants.CACHE_USER_BALANCE,
            defaultCacheConfig.entryTtl(Duration.ofMinutes(CommonConstants.CACHE_USER_BALANCE_TTL)))
        .build();
  }
}
