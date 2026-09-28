package com.onlinejudge.executionservice.controller;

import com.onlinejudge.executionservice.dto.ExecutionRequest;
import com.onlinejudge.executionservice.dto.ExecutionResult;
import com.onlinejudge.executionservice.service.CodeExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExecutionController {

    private final CodeExecutionService codeExecutionService;

    @PostMapping("/api/execute")
    public ExecutionResult execute(@Valid @RequestBody ExecutionRequest request) {
        return codeExecutionService.execute(request);
    }
}
