package com.onlinejudge.executionservice.dto;

import com.onlinejudge.executionservice.model.VerdictStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionResult {
    private Long submissionId; // correlation id - matched back to the Submission in problem-service
    private VerdictStatus status;
    private Integer runtimeMs;
    private Integer memoryKb;
    private String failedTestCaseInfo;
}
