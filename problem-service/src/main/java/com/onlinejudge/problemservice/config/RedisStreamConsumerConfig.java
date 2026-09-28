package com.onlinejudge.problemservice.config;

import com.onlinejudge.problemservice.listener.VerdictStreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
public class RedisStreamConsumerConfig {

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> verdictStreamContainer(
            RedisConnectionFactory connectionFactory, VerdictStreamListener listener) {

        ensureGroup(connectionFactory, VerdictStreamListener.STREAM_KEY, VerdictStreamListener.GROUP_NAME);

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .<String, MapRecord<String, String, String>>build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        container.receive(
                Consumer.from(VerdictStreamListener.GROUP_NAME, "problem-service-1"),
                StreamOffset.create(VerdictStreamListener.STREAM_KEY, ReadOffset.lastConsumed()),
                listener);

        container.start();
        return container;
    }

    // Equivalent to `XGROUP CREATE <stream> <group> $ MKSTREAM` - creates the
    // stream (if missing) and the consumer group (if missing) on startup.
    // In a real deployment you'd run this once via ops tooling instead.
    private void ensureGroup(RedisConnectionFactory factory, String streamKey, String groupName) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.afterPropertiesSet();
        try {
            template.opsForStream().createGroup(streamKey, groupName);
        } catch (Exception e) {
            // group (and stream) already exist past the first startup - fine
        }
    }
}
