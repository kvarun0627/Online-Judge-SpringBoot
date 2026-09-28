package com.onlinejudge.problemservice.client;

import com.onlinejudge.problemservice.entity.VerdictStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionResult {
    private Long submissionId; // correlation id - which Submission this verdict belongs to
    private VerdictStatus status;
    private Integer runtimeMs;
    private Integer memoryKb;
    private String failedTestCaseInfo;
}
