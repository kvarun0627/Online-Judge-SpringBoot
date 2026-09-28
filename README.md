# Online Judge — Phased Build Plan

Two Spring Boot microservices:

- **problem-service** (port 8080) — owns the DB: Questions, TestCases, Submissions.
  Admin CRUD + solver-facing read APIs + submission orchestration.
- **execution-service** (port 8081) — stateless: compiles/runs submitted code
  against test cases and returns a verdict. No database.

They talk over plain REST today (`problem-service` calls `execution-service`
via `ExecutionClient`); the contract is designed so that boundary can become
async (Kafka/RabbitMQ) later without touching business logic.

## Design patterns used (and why)

| Pattern | Where | Problem it solves |
|---|---|---|
| Strategy | `execution-service/strategy/*Executor` | One compile/run recipe per language, added without touching existing code (OCP) |
| Template Method | `AbstractProcessLanguageExecutor` | Shared "spawn a process, wait, capture output" logic reused by every language |
| Factory | `LanguageExecutorFactory` | Picks the right `LanguageExecutor` strategy at runtime from the `Language` enum |
| Chain of Responsibility | `verdict/*Handler` | Verdict decision (TLE → Runtime Error → Wrong Answer → Accepted) as an ordered, independently-extensible pipeline |
| Repository | `problem-service/repository/*` | Persistence hidden behind Spring Data interfaces |
| Adapter (port/adapter) | `ExecutionClient` (interface) + `ExecutionClientImpl` (HTTP) | `SubmissionService` depends on an abstraction, not on WebClient/HTTP directly — swappable for a queue-based adapter in Phase 6 |
| Interface Segregation | `QuestionCommandService` vs `QuestionQueryService` | Admin write path and everyone's read path are separate contracts, so a read-only consumer never depends on write methods |
| DTO/Mapper (SRP) | `QuestionMapper` | Entity↔API shape conversion isolated from business logic; hidden test cases are filtered out only here |
| Dependency Inversion | Every `*ServiceImpl` | Depends on interfaces (`QuestionQueryService`, `ExecutionClient`, `VerdictHandler`) injected by Spring, not concrete classes |

## Phases

**Phase 1 — Skeleton & data model (done here)**
Two Maven projects, entities (`Question`, `TestCase`, `Submission`), H2 in-memory DB, `application.yml`.

**Phase 2 — Admin CRUD (done here)**
`AdminQuestionController`: create/update/delete questions with nested test cases.
`QuestionCommandService` / `QuestionServiceImpl`.

**Phase 3 — Solver-facing read + submission flow (done here)**
`QuestionController` (list/detail, hidden test cases stripped by `QuestionMapper`),
`SubmissionController` → `SubmissionService` → `ExecutionClient` → execution-service,
verdict persisted on `Submission`.

**Phase 4 — Execution service: compile & run per language (done here)**
Strategy classes for C++ (`g++`), Java (`javac`/`java`, class must be named `Main`),
Python (`python3`, no compile step). `ProcessRunner` executes with a wall-clock
timeout and captures stdout/stderr/exit code.

**Phase 5 — Verdict generation (done here)**
Chain of Responsibility: TLE → Runtime Error → Wrong Answer (whitespace-normalized
diff) → Accepted. First failing test case short-circuits the run.

**Phase 6 — Make submission async (done here, via Redis Streams)**
`ExecutionClient` (synchronous HTTP/Feign) is gone. `SubmissionServiceImpl` now
depends on `SubmissionEventPublisher`, which does `XADD` onto the
`oj:submissions:queued` Redis stream and returns immediately with a `PENDING`
submission. `execution-service`'s `SubmissionStreamListener` reads that stream
via a consumer group, runs the code, and does `XADD` onto `oj:verdicts:ready`
through `VerdictEventPublisher`. `problem-service`'s `VerdictStreamListener`
consumes that second stream and calls `SubmissionVerdictUpdater.applyVerdict()`
to update the `Submission` row. `SubmissionService`'s public contract (`submit`,
`getSubmission`) never changed — only what happens between "saved" and
"verdict known" did. The client now gets `PENDING` back immediately and must
poll `GET /api/submissions/{id}` (or you add a WebSocket/SSE push later).

*Why Redis Streams and not Pub/Sub*: Pub/Sub messages aren't persisted — if a
subscriber isn't connected at publish time, the message is gone. Streams
persist to Redis and, combined with a consumer group, track a durable
per-group read offset server-side. That's what lets execution-service restart
and resume exactly where it left off instead of replaying everything or
losing what arrived while it was down; crashed-mid-processing messages sit in
the Pending Entries List instead of vanishing.

**Phase 7 — Real sandboxing & resource limits**
Swap `ProcessRunner` for a Docker-based runner: `--network=none`, `--memory`,
`--cpus`, `--pids-limit`, read-only root filesystem, non-root user, one
container per run. This is the single most important phase before letting
untrusted users submit code — the current `ProcessBuilder` implementation
shares the host kernel and is not safe for production/public use.

**Phase 8 — Auth & roles**
Spring Security + JWT. `ADMIN` role required on `/api/admin/**`
(`@PreAuthorize("hasRole('ADMIN')")`), `USER` role for submissions.
Replace the free-text `userId` field in `SubmissionRequest` with the
authenticated principal.

**Phase 9 — Hardening & ops**
Rate limiting per user (submission spam), submission queue backpressure,
structured logging/metrics (Micrometer), centralized config (Spring Cloud
Config or just env vars), containerize both services, docker-compose /
Kubernetes manifests, CI pipeline (build, test, lint).

## Running Phases 1–5 locally

```bash
# needs: JDK 17, Maven, g++, python3 all on PATH
cd execution-service && mvn spring-boot:run   # port 8081
cd problem-service   && mvn spring-boot:run   # port 8080
```

Create a question (Admin):
```
POST http://localhost:8080/api/admin/questions
{
  "title": "Sum of two numbers",
  "statement": "Read two integers, print their sum.",
  "timeLimitMs": 2000,
  "memoryLimitKb": 65536,
  "difficulty": "EASY",
  "testCases": [
    {"input": "2 3\n", "expectedOutput": "5\n", "hidden": false},
    {"input": "10 20\n", "expectedOutput": "30\n", "hidden": true}
  ]
}
```

Submit a solution:
```
POST http://localhost:8080/api/questions/1/submissions
{
  "userId": "varun",
  "language": "PYTHON",
  "sourceCode": "a,b=map(int,input().split());print(a+b)"
}
```
→ returns `{"status":"ACCEPTED", ...}` or a rejected verdict with `failedTestCaseInfo`.

## What's a stub vs what actually works

- Compile/run/verdict pipeline: **fully functional** for CPP/JAVA/PYTHON via local
  toolchains (`g++`, `javac`+`java`, `python3` must be installed wherever
  execution-service runs).
- Memory limit enforcement: **not implemented** (`memoryKb` is a placeholder `0`) —
  needs Docker/cgroups (Phase 7) for real measurement and enforcement.
- Auth: **not implemented** (Phase 8) — every endpoint is currently open.
- Async queue: **not implemented** (Phase 6) — submissions block on the HTTP call
  to execution-service until a verdict comes back.
