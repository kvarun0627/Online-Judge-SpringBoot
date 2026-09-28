package com.onlinejudge.problemservice.controller;

import com.onlinejudge.problemservice.dto.QuestionRequest;
import com.onlinejudge.problemservice.service.QuestionCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NOTE: add @PreAuthorize("hasRole('ADMIN')") here once Phase 8 (Auth) is wired in.
@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionCommandService questionCommandService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody QuestionRequest request) {
        Long id = questionCommandService.createQuestion(request);
        return ResponseEntity.status(201).body(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        questionCommandService.updateQuestion(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionCommandService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
