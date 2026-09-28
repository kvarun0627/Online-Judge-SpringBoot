package com.onlinejudge.problemservice.controller;

import com.onlinejudge.problemservice.dto.QuestionDetailResponse;
import com.onlinejudge.problemservice.dto.QuestionSummaryResponse;
import com.onlinejudge.problemservice.service.QuestionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionQueryService questionQueryService;

    @GetMapping
    public List<QuestionSummaryResponse> list() {
        return questionQueryService.listQuestions();
    }

    @GetMapping("/{id}")
    public QuestionDetailResponse get(@PathVariable Long id) {
        return questionQueryService.getQuestionDetail(id);
    }
}
