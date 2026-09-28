package com.onlinejudge.executionservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinejudge.executionservice.dto.ExecutionResult;
import com.onlinejudge.executionservice.service.VerdictEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisVerdictEventPublisher implements VerdictEventPublisher {

    public static final String STREAM_KEY = "oj:verdicts:ready";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(ExecutionResult result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForStream().add(STREAM_KEY, Map.of("payload", json));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize verdict", e);
        }
    }
}
