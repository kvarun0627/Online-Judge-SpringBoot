package com.onlinejudge.problemservice.dto;

import com.onlinejudge.problemservice.entity.VerdictStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmissionResponse {
    private Long submissionId;
    private VerdictStatus status;
    private Integer runtimeMs;
    private Integer memoryKb;
    private String failedTestCaseInfo;
}
