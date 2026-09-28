package com.onlinejudge.problemservice.dto;

import com.onlinejudge.problemservice.entity.Difficulty;
import lombok.*;

import java.util.List;

// Detail view for a solver: only SAMPLE (non-hidden) test cases are included.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionDetailResponse {
    private Long id;
    private String title;
    private String statement;
    private Integer timeLimitMs;
    private Integer memoryLimitKb;
    private Difficulty difficulty;
    private List<TestCaseDto> sampleTestCases;
}
