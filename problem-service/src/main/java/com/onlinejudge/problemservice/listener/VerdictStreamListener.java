package com.onlinejudge.problemservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinejudge.problemservice.client.ExecutionResult;
import com.onlinejudge.problemservice.service.SubmissionVerdictUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerdictStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    public static final String STREAM_KEY = "oj:verdicts:ready";
    public static final String GROUP_NAME = "problem-service-group";

    private final ObjectMapper objectMapper;
    private final SubmissionVerdictUpdater submissionVerdictUpdater;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        try {
            ExecutionResult result = objectMapper.readValue(record.getValue().get("payload"), ExecutionResult.class);
            submissionVerdictUpdater.applyVerdict(result);
            // ack only after the DB update succeeds - if this throws first, the
            // message stays pending and gets redelivered instead of silently lost.
            redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP_NAME, record.getId());
        } catch (Exception e) {
            log.error("Failed to process verdict event {}", record.getId(), e);
        }
    }
}
