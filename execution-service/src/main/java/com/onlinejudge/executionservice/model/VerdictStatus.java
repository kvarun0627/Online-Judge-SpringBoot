package com.onlinejudge.executionservice.model;

public enum VerdictStatus {
    PENDING,
    RUNNING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    RUNTIME_ERROR,
    COMPILATION_ERROR,
    INTERNAL_ERROR
}
