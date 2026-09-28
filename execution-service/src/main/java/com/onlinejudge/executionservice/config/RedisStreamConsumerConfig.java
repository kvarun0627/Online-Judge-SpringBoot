package com.onlinejudge.executionservice.config;

import com.onlinejudge.executionservice.listener.SubmissionStreamListener;
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
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> submissionStreamContainer(
            RedisConnectionFactory connectionFactory, SubmissionStreamListener listener) {

        ensureGroup(connectionFactory, SubmissionStreamListener.STREAM_KEY, SubmissionStreamListener.GROUP_NAME);

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .<String, MapRecord<String, String, String>>build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        // ReadOffset.lastConsumed() with a consumer group means "give me
        // whatever this group hasn't been delivered yet" - Redis tracks that
        // pointer server-side, keyed by group name, independent of any one
        // consumer process being up or down.
        container.receive(
                Consumer.from(SubmissionStreamListener.GROUP_NAME, "execution-service-1"),
                StreamOffset.create(SubmissionStreamListener.STREAM_KEY, ReadOffset.lastConsumed()),
                listener);

        container.start();
        return container;
    }

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
