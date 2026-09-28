package com.onlinejudge.executionservice.strategy;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompilationOutcome {
    private boolean success;
    private String errorMessage;

    public static CompilationOutcome ok() {
        return CompilationOutcome.builder().success(true).build();
    }

    public static CompilationOutcome failed(String message) {
        return CompilationOutcome.builder().success(false).errorMessage(message).build();
    }
}
