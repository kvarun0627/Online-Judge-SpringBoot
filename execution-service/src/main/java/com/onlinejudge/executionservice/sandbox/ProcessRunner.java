package com.onlinejudge.executionservice.sandbox;

import com.onlinejudge.executionservice.model.RunOutcome;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Runs a single compiled/interpreted program against one test case with a wall-clock
 * time limit, and captures stdout/stderr/exit code/elapsed time.
 *
 * PRODUCTION NOTE (Phase 7): this ProcessBuilder approach shares the host kernel with
 * the judge itself - fine for a learning project, NOT safe for untrusted code in
 * production. Harden it by running each submission inside a locked-down Docker
 * container (--network=none, --memory, --cpus, --pids-limit, read-only rootfs,
 * non-root user) instead of a bare ProcessBuilder. The DockerSandboxRunner class
 * below sketches that swap - same interface, different implementation.
 */
@Component
public class ProcessRunner {

    public RunOutcome run(List<String> command, Path workDir, String input,
                           int timeLimitMs, int testCaseIndex) {
        long start = System.currentTimeMillis();
        try {
            ProcessBuilder pb = new ProcessBuilder(command).directory(workDir.toFile());
            Process process = pb.start();

            try (var stdin = process.getOutputStream()) {
                stdin.write(input.getBytes(StandardCharsets.UTF_8));
                stdin.flush();
            } catch (IOException ignored) {
                // program may not read stdin at all; not an error by itself
            }

            boolean finished = process.waitFor(timeLimitMs, TimeUnit.MILLISECONDS);
            long elapsed = System.currentTimeMillis() - start;

            if (!finished) {
                process.destroyForcibly();
                return RunOutcome.builder()
                        .compiled(true)
                        .timedOut(true)
                        .runtimeMs((int) elapsed)
                        .testCaseIndex(testCaseIndex)
                        .build();
            }

            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            boolean crashed = process.exitValue() != 0;

            return RunOutcome.builder()
                    .compiled(true)
                    .timedOut(false)
                    .crashed(crashed)
                    .stdout(stdout)
                    .stderr(stderr)
                    .runtimeMs((int) elapsed)
                    .memoryKb(0) // best-effort only without cgroups/Docker stats - see Phase 7
                    .testCaseIndex(testCaseIndex)
                    .build();

        } catch (IOException | InterruptedException e) {
            return RunOutcome.builder()
                    .compiled(true)
                    .crashed(true)
                    .stderr(e.getMessage())
                    .runtimeMs((int) (System.currentTimeMillis() - start))
                    .testCaseIndex(testCaseIndex)
                    .build();
        }
    }
}
