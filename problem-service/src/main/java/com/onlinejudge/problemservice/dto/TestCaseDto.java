package com.onlinejudge.problemservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestCaseDto {
    private Long id;

    @NotBlank
    private String input;

    @NotBlank
    private String expectedOutput;

    private boolean hidden;
}
