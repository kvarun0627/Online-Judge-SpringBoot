package com.onlinejudge.problemservice.dto;

import com.onlinejudge.problemservice.entity.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

// Used for both create and update (Admin only).
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String statement;

    @Min(100)
    private Integer timeLimitMs;

    @Min(1024)
    private Integer memoryLimitKb;

    private Difficulty difficulty;

    @NotEmpty
    private List<@Valid TestCaseDto> testCases;
}
