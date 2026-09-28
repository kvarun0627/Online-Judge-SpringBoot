package com.onlinejudge.executionservice.service.impl;

import com.onlinejudge.executionservice.dto.ExecutionRequest;
import com.onlinejudge.executionservice.dto.ExecutionResult;
import com.onlinejudge.executionservice.dto.TestCaseDto;
import com.onlinejudge.executionservice.factory.LanguageExecutorFactory;
import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;
import com.onlinejudge.executionservice.sandbox.ProcessRunner;
import com.onlinejudge.executionservice.service.CodeExecutionService;
import com.onlinejudge.executionservice.strategy.CompilationOutcome;
import com.onlinejudge.executionservice.strategy.LanguageExecutor;
import com.onlinejudge.executionservice.verdict.VerdictHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

// Orchestrator: depends only on abstractions (LanguageExecutor via the factory,
// VerdictHandler chain, ProcessRunner) - each of which can be unit-tested or
// swapped independently. This class's only job is to wire the pipeline together
// (Single Responsibility at the orchestration level).
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionServiceImpl implements CodeExecutionService {

    private final LanguageExecutorFactory executorFactory;
    private final ProcessRunner processRunner;
    private final VerdictHandler verdictChain;

    @Override
    public ExecutionResult execute(ExecutionRequest request) {
        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("submission-" + request.getSubmissionId());
            LanguageExecutor executor = executorFactory.getExecutor(request.getLanguage());

            CompilationOutcome compilation = executor.compile(request.getSourceCode(), workDir);
            if (!compilation.isSuccess()) {
                return ExecutionResult.builder()
                        .status(VerdictStatus.COMPILATION_ERROR)
                        .failedTestCaseInfo(compilation.getErrorMessage())
                        .build();
            }

            int worstRuntime = 0;
            for (int i = 0; i < request.getTestCases().size(); i++) {
                TestCaseDto testCase = request.getTestCases().get(i);

                RunOutcome outcome = processRunner.run(executor.runCommand(workDir), workDir, testCase.getInput(), request.getTimeLimitMs(), i);
                outcome.setExpectedOutput(testCase.getExpectedOutput());

                VerdictStatus verdict = verdictChain.handle(outcome);
                worstRuntime = Math.max(worstRuntime, outcome.getRuntimeMs());

                if (verdict != VerdictStatus.ACCEPTED) {
                    return ExecutionResult.builder()
                            .status(verdict)
                            .runtimeMs(outcome.getRuntimeMs())
                            .memoryKb(outcome.getMemoryKb())
                            .failedTestCaseInfo("Failed on test case #" + (i + 1))
                            .build();
                }
            }

            return ExecutionResult.builder()
                    .status(VerdictStatus.ACCEPTED)
                    .runtimeMs(worstRuntime)
                    .memoryKb(0)
                    .build();

        } catch (IOException | InterruptedException e) {
            log.error("Execution failed for submission {}", request.getSubmissionId(), e);
            return ExecutionResult.builder()
                    .status(VerdictStatus.INTERNAL_ERROR)
                    .failedTestCaseInfo(e.getMessage())
                    .build();
        } finally {
            cleanup(workDir);
        }
    }

    private void cleanup(Path workDir) {
        if (workDir == null) return;
        try (var paths = Files.walk(workDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
