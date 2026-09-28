package com.onlinejudge.problemservice.client;

import com.onlinejudge.problemservice.entity.Language;
import lombok.*;

import java.util.List;

// Sent to execution-service: everything it needs, nothing it shouldn't have
// (e.g. no DB entities leak across the service boundary).
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionRequest {
    private Long submissionId;
    private Language language;
    private String sourceCode;
    private Integer timeLimitMs;
    private Integer memoryLimitKb;
    private List<ExecutionTestCase> testCases;
}
