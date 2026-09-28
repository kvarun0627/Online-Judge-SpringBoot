package com.onlinejudge.executionservice.dto;

import com.onlinejudge.executionservice.model.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionRequest {
    @NotNull
    private Long submissionId;

    @NotNull
    private Language language;

    @NotBlank
    private String sourceCode;

    @NotNull
    private Integer timeLimitMs;

    @NotNull
    private Integer memoryLimitKb;

    @NotEmpty
    private List<TestCaseDto> testCases;
}
