package com.onlinejudge.executionservice.strategy;

import com.onlinejudge.executionservice.model.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Strategy pattern: one implementation per language. Adding a new language
// (Phase extension) means adding one new class - Open/Closed Principle.
public interface LanguageExecutor {

    Language supportedLanguage();

    // Writes source to workDir and compiles it (no-op for interpreted languages).
    CompilationOutcome compile(String sourceCode, Path workDir) throws IOException, InterruptedException;

    // Command used to run the program once per test case, with workDir as CWD.
    // Used by the bare ProcessRunner (host process, no sandbox).
    List<String> runCommand(Path workDir);
}
