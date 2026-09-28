package com.onlinejudge.executionservice.service;

import com.onlinejudge.executionservice.dto.ExecutionResult;

public interface VerdictEventPublisher {
    void publish(ExecutionResult result);
}
