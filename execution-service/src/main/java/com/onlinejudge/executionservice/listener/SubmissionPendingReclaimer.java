package com.onlinejudge.executionservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionPendingReclaimer {

    private static final Duration IDLE_THRESHOLD = Duration.ofSeconds(30);
    private static final String RECLAIMER_CONSUMER = "reclaimer";

    private final StringRedisTemplate redisTemplate;
    private final SubmissionStreamListener listener;

    @Scheduled(fixedDelay = 15000)
    public void reclaimStalePendingEntries() {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();

        PendingMessages pending = streamOps.pending(
                SubmissionStreamListener.STREAM_KEY,
                SubmissionStreamListener.GROUP_NAME,
                Range.unbounded(),
                50);

        if (pending == null || pending.isEmpty()) {
            return;
        }

        List<RecordId> staleIds = pending.stream()
                .filter(p -> p.getElapsedTimeSinceLastDelivery().compareTo(IDLE_THRESHOLD) > 0)
                .map(PendingMessage::getId)
                .toList();

        if (staleIds.isEmpty()) {
            return;
        }

        log.warn("Reclaiming {} stale pending submission(s): {}", staleIds.size(), staleIds);

        List<MapRecord<String, String, String>> claimed = streamOps.claim(
                SubmissionStreamListener.STREAM_KEY,
                SubmissionStreamListener.GROUP_NAME,
                RECLAIMER_CONSUMER,
                IDLE_THRESHOLD,
                staleIds.toArray(new RecordId[0]));

        claimed.forEach(listener::onMessage);
    }
}