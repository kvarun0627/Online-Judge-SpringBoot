package com.onlinejudge.problemservice.service;

import com.onlinejudge.problemservice.dto.SubmissionRequest;
import com.onlinejudge.problemservice.dto.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse submit(Long questionId, SubmissionRequest request);
    SubmissionResponse getSubmission(Long submissionId);
}
