package com.onlinejudge.problemservice.client;

// Replaces ExecutionClient. Fire-and-forget: publishing does not return a
// result, because there isn't one yet - the verdict arrives later via
// VerdictStreamListener. SubmissionServiceImpl depends on this abstraction,
// not on Redis directly, so swapping Redis Streams for Kafka later only
// means writing a new implementation of this interface.
public interface SubmissionEventPublisher {
    void publish(ExecutionRequest request);
}
