package com.onlinejudge.problemservice.service;

import com.onlinejudge.problemservice.dto.*;
import com.onlinejudge.problemservice.entity.Question;
import com.onlinejudge.problemservice.entity.TestCase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// Single Responsibility: entity <-> DTO conversion lives here only.
@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        Question question = Question.builder()
                .title(request.getTitle())
                .statement(request.getStatement())
                .timeLimitMs(request.getTimeLimitMs())
                .memoryLimitKb(request.getMemoryLimitKb())
                .difficulty(request.getDifficulty())
                .build();

        request.getTestCases().forEach(tc -> question.addTestCase(
                TestCase.builder()
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .hidden(tc.isHidden())
                        .build()));
        return question;
    }

    public QuestionSummaryResponse toSummary(Question q) {
        return QuestionSummaryResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .difficulty(q.getDifficulty())
                .build();
    }

    // Solver-facing: hidden test cases are filtered out on purpose.
    public QuestionDetailResponse toDetail(Question q) {
        List<TestCaseDto> samples = q.getTestCases().stream()
                .filter(tc -> !tc.isHidden())
                .map(tc -> TestCaseDto.builder()
                        .id(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .hidden(false)
                        .build())
                .collect(Collectors.toList());

        return QuestionDetailResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .statement(q.getStatement())
                .timeLimitMs(q.getTimeLimitMs())
                .memoryLimitKb(q.getMemoryLimitKb())
                .difficulty(q.getDifficulty())
                .sampleTestCases(samples)
                .build();
    }
}
