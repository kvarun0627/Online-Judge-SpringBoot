package com.onlinejudge.problemservice.dto;

import com.onlinejudge.problemservice.entity.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmissionRequest {
    @NotNull
    private Language language;

    @NotBlank
    private String sourceCode;

    @NotBlank
    private String userId;
}
