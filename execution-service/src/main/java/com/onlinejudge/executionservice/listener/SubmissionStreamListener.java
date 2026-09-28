package com.onlinejudge.executionservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinejudge.executionservice.dto.ExecutionRequest;
import com.onlinejudge.executionservice.dto.ExecutionResult;
import com.onlinejudge.executionservice.service.CodeExecutionService;
import com.onlinejudge.executionservice.service.VerdictEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    public static final String STREAM_KEY = "oj:submissions:queued";
    public static final String GROUP_NAME = "execution-service-group";

    private final ObjectMapper objectMapper;
    private final CodeExecutionService codeExecutionService;
    private final VerdictEventPublisher verdictEventPublisher;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        try {
            ExecutionRequest request = objectMapper.readValue(
                    record.getValue().get("payload"), ExecutionRequest.class);

            ExecutionResult result = codeExecutionService.execute(request);
            result.setSubmissionId(request.getSubmissionId());

            verdictEventPublisher.publish(result);

            // Ack only after the verdict has been published successfully - if
            // publish() throws, this record stays pending and is retried
            // (see the PEL note below) instead of being silently dropped.
            redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP_NAME, record.getId());
        } catch (Exception e) {
            log.error("Failed to process submission event {}", record.getId(), e);
        }
    }
}
