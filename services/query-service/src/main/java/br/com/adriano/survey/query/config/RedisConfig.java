package br.com.adriano.survey.query.config;

import br.com.adriano.survey.query.dto.RestaurantRatingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper mapper;

    @Bean
    RedisTemplate<String, RestaurantRatingResponse> redisTemplate(
            RedisConnectionFactory factory
    ) {

        RedisTemplate<String, RestaurantRatingResponse> template =
                new RedisTemplate<>();

        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<RestaurantRatingResponse> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, RestaurantRatingResponse.class);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
