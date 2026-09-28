package com.onlinejudge.executionservice.service;

import com.onlinejudge.executionservice.dto.ExecutionRequest;
import com.onlinejudge.executionservice.dto.ExecutionResult;

public interface CodeExecutionService {
    ExecutionResult execute(ExecutionRequest request);
}
