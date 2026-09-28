package com.onlinejudge.executionservice.strategy;

import com.onlinejudge.executionservice.model.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CppExecutor extends AbstractProcessLanguageExecutor {

    @Override
    public Language supportedLanguage() {
        return Language.CPP;
    }

    @Override
    public CompilationOutcome compile(String sourceCode, Path workDir) throws IOException, InterruptedException {
        Path source = workDir.resolve("Solution.cpp");
        Files.writeString(source, sourceCode);
        return runCompileCommand(
                List.of("g++", "-O2", "-o", "solution", "Solution.cpp"),
                workDir);
    }

    @Override
    public List<String> runCommand(Path workDir) {
        return List.of(workDir.resolve("solution").toAbsolutePath().toString());
    }
}
