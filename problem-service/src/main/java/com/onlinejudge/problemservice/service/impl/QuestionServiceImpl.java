package com.onlinejudge.problemservice.service.impl;

import com.onlinejudge.problemservice.dto.*;
import com.onlinejudge.problemservice.entity.Question;
import com.onlinejudge.problemservice.exception.QuestionNotFoundException;
import com.onlinejudge.problemservice.repository.QuestionRepository;
import com.onlinejudge.problemservice.service.QuestionCommandService;
import com.onlinejudge.problemservice.service.QuestionMapper;
import com.onlinejudge.problemservice.service.QuestionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// One class can implement several narrow interfaces (ISP) while depending
// only on abstractions it needs internally (repository, mapper) -> DIP.
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionCommandService, QuestionQueryService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper mapper;

    @Override
    @Transactional
    public Long createQuestion(QuestionRequest request) {
        Question question = mapper.toEntity(request);
        return questionRepository.save(question).getId();
    }

    @Override
    @Transactional
    public void updateQuestion(Long id, QuestionRequest request) {
        Question existing = getQuestionEntityOrThrow(id);
        existing.setTitle(request.getTitle());
        existing.setStatement(request.getStatement());
        existing.setTimeLimitMs(request.getTimeLimitMs());
        existing.setMemoryLimitKb(request.getMemoryLimitKb());
        existing.setDifficulty(request.getDifficulty());

        existing.getTestCases().clear();
        Question rebuilt = mapper.toEntity(request);
        rebuilt.getTestCases().forEach(existing::addTestCase);

        questionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new QuestionNotFoundException(id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryResponse> listQuestions() {
        return questionRepository.findAll().stream().map(mapper::toSummary).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionDetailResponse getQuestionDetail(Long id) {
        return mapper.toDetail(getQuestionEntityOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Question getQuestionEntityOrThrow(Long id) {
        return questionRepository.findWithTestCasesById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
    }
}
