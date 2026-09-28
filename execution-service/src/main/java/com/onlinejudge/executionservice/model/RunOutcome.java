package com.onlinejudge.executionservice.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RunOutcome {
    private boolean compiled;
    private String compileError;

    private boolean timedOut;
    private boolean crashed;       // non-zero exit code
    private String stderr;
    private String stdout;
    private String expectedOutput;
    private int runtimeMs;
    private int memoryKb;
    private int testCaseIndex;
}
