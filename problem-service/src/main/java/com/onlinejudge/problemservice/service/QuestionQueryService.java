package com.onlinejudge.problemservice.service;

import com.onlinejudge.problemservice.dto.QuestionDetailResponse;
import com.onlinejudge.problemservice.dto.QuestionSummaryResponse;
import com.onlinejudge.problemservice.entity.Question;

import java.util.List;

// Read side - used by both solver-facing and internal (submission judging) code.
public interface QuestionQueryService {
    List<QuestionSummaryResponse> listQuestions();
    QuestionDetailResponse getQuestionDetail(Long id);
    Question getQuestionEntityOrThrow(Long id); // used internally, e.g. by SubmissionService
}
