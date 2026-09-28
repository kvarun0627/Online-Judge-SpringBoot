package com.onlinejudge.executionservice.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestCaseDto {
    private String input;
    private String expectedOutput;
}
