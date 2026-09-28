package com.onlinejudge.problemservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisSubmissionEventPublisher implements SubmissionEventPublisher {

    public static final String STREAM_KEY = "oj:submissions:queued";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(ExecutionRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            redisTemplate.opsForStream().add(STREAM_KEY, Map.of("payload", json));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize execution request", e);
        }
    }
}
