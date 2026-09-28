package com.onlinejudge.problemservice.service.impl;

import com.onlinejudge.problemservice.client.ExecutionRequest;
import com.onlinejudge.problemservice.client.ExecutionResult;
import com.onlinejudge.problemservice.client.ExecutionTestCase;
import com.onlinejudge.problemservice.client.SubmissionEventPublisher;
import com.onlinejudge.problemservice.dto.SubmissionRequest;
import com.onlinejudge.problemservice.dto.SubmissionResponse;
import com.onlinejudge.problemservice.entity.Question;
import com.onlinejudge.problemservice.entity.Submission;
import com.onlinejudge.problemservice.exception.SubmissionNotFoundException;
import com.onlinejudge.problemservice.repository.SubmissionRepository;
import com.onlinejudge.problemservice.service.QuestionQueryService;
import com.onlinejudge.problemservice.service.SubmissionService;
import com.onlinejudge.problemservice.service.SubmissionVerdictUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService, SubmissionVerdictUpdater {

    private final SubmissionRepository submissionRepository;
    private final QuestionQueryService questionQueryService;
    private final SubmissionEventPublisher submissionEventPublisher; // was ExecutionClient

    @Override
    @Transactional
    public SubmissionResponse submit(Long questionId, SubmissionRequest request) {
        Question question = questionQueryService.getQuestionEntityOrThrow(questionId);

        Submission submission = Submission.builder()
                .questionId(questionId)
                .userId(request.getUserId())
                .language(request.getLanguage())
                .sourceCode(request.getSourceCode())
                .build(); // status defaults to PENDING
        submission = submissionRepository.save(submission);

        // Fire-and-forget: publish and return immediately. The verdict is not
        // known yet - it arrives later via applyVerdict(), called from
        // VerdictStreamListener once execution-service publishes it.
        submissionEventPublisher.publish(buildExecutionRequest(submission, question));

        return toResponse(submission); // status: PENDING
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));
        return toResponse(submission);
    }

    @Override
    @Transactional
    public void applyVerdict(ExecutionResult result) {
        Submission submission = submissionRepository.findById(result.getSubmissionId())
                .orElseThrow(() -> new SubmissionNotFoundException(result.getSubmissionId()));
        submission.setStatus(result.getStatus());
        submission.setRuntimeMs(result.getRuntimeMs());
        submission.setMemoryKb(result.getMemoryKb());
        submission.setFailedTestCaseInfo(result.getFailedTestCaseInfo());
        submissionRepository.save(submission);
    }

    private ExecutionRequest buildExecutionRequest(Submission submission, Question question) {
        List<ExecutionTestCase> testCases = question.getTestCases().stream()
                .map(tc -> ExecutionTestCase.builder()
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .build())
                .toList();

        return ExecutionRequest.builder()
                .submissionId(submission.getId())
                .language(submission.getLanguage())
                .sourceCode(submission.getSourceCode())
                .timeLimitMs(question.getTimeLimitMs())
                .memoryLimitKb(question.getMemoryLimitKb())
                .testCases(testCases)
                .build();
    }

    private SubmissionResponse toResponse(Submission s) {
        return SubmissionResponse.builder()
                .submissionId(s.getId())
                .status(s.getStatus())
                .runtimeMs(s.getRuntimeMs())
                .memoryKb(s.getMemoryKb())
                .failedTestCaseInfo(s.getFailedTestCaseInfo())
                .build();
    }
}
