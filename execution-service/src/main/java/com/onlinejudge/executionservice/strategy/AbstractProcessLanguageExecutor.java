package com.onlinejudge.executionservice.strategy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Template Method pattern: shared "compile via external process" flow;
// subclasses supply the specific compile command (or none, for interpreted languages).
public abstract class AbstractProcessLanguageExecutor implements LanguageExecutor {

    protected static final int COMPILE_TIMEOUT_SECONDS = 10;

    protected CompilationOutcome runCompileCommand(List<String> command, Path workDir)
            throws IOException, InterruptedException {
        if (command.isEmpty()) {
            return CompilationOutcome.ok(); // interpreted language, nothing to compile
        }
        ProcessBuilder pb = new ProcessBuilder(command)
                .directory(workDir.toFile())
                .redirectErrorStream(true);
        Process process = pb.start();
        boolean finished = process.waitFor(COMPILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return CompilationOutcome.failed("Compilation timed out");
        }
        String output = new String(process.getInputStream().readAllBytes());
        if (process.exitValue() != 0) {
            return CompilationOutcome.failed(output);
        }
        return CompilationOutcome.ok();
    }
}
