package com.onlinejudge.problemservice.controller;

import com.onlinejudge.problemservice.dto.SubmissionRequest;
import com.onlinejudge.problemservice.dto.SubmissionResponse;
import com.onlinejudge.problemservice.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/api/questions/{questionId}/submissions")
    public SubmissionResponse submit(@PathVariable Long questionId,
                                      @Valid @RequestBody SubmissionRequest request) {
        return submissionService.submit(questionId, request);
    }

    @GetMapping("/api/submissions/{id}")
    public SubmissionResponse get(@PathVariable Long id) {
        return submissionService.getSubmission(id);
    }
}
