package com.onlinejudge.executionservice.strategy;

import com.onlinejudge.executionservice.model.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Convention: the submitted class must be named "Main" with a public static void main.
@Component
public class JavaExecutor extends AbstractProcessLanguageExecutor {

    @Override
    public Language supportedLanguage() {
        return Language.JAVA;
    }

    @Override
    public CompilationOutcome compile(String sourceCode, Path workDir) throws IOException, InterruptedException {
        Path source = workDir.resolve("Main.java");
        Files.writeString(source, sourceCode);
        return runCompileCommand(List.of("javac", "Main.java"), workDir);
    }

    @Override
    public List<String> runCommand(Path workDir) {
        return List.of("java", "-cp", workDir.toAbsolutePath().toString(), "Main");
    }
}
