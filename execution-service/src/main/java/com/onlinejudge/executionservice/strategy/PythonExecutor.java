package com.onlinejudge.executionservice.strategy;

import com.onlinejudge.executionservice.model.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class PythonExecutor extends AbstractProcessLanguageExecutor {

    @Override
    public Language supportedLanguage() {
        return Language.PYTHON;
    }

    @Override
    public CompilationOutcome compile(String sourceCode, Path workDir) throws IOException, InterruptedException {
        Path source = workDir.resolve("solution.py");
        Files.writeString(source, sourceCode);
        return CompilationOutcome.ok(); // interpreted: nothing to compile
    }

    @Override
    public List<String> runCommand(Path workDir) {
        return List.of("python", workDir.resolve("solution.py").toAbsolutePath().toString());
    }
}
