package com.onlinejudge.problemservice.dto;

import com.onlinejudge.problemservice.entity.Difficulty;
import lombok.*;

// Lightweight view for list endpoints - never leaks hidden test cases.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionSummaryResponse {
    private Long id;
    private String title;
    private Difficulty difficulty;
}
