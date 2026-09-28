package com.onlinejudge.problemservice.client;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExecutionTestCase {
    private String input;
    private String expectedOutput;
}
