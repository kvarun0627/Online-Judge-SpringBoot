package com.onlinejudge.problemservice.service;

import com.onlinejudge.problemservice.dto.QuestionRequest;

// Write side - only Admin-facing controllers depend on this.
public interface QuestionCommandService {
    Long createQuestion(QuestionRequest request);
    void updateQuestion(Long id, QuestionRequest request);
    void deleteQuestion(Long id);
}
