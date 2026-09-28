package com.onlinejudge.executionservice.verdict;

import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;
import org.springframework.stereotype.Component;

@Component
public class WrongAnswerHandler extends VerdictHandler {
    @Override
    protected VerdictStatus evaluate(RunOutcome outcome) {
        String actual = normalize(outcome.getStdout());
        String expected = normalize(outcome.getExpectedOutput());
        return actual.equals(expected) ? null : VerdictStatus.WRONG_ANSWER;
    }

    // Trim trailing whitespace/newlines per line so formatting noise doesn't
    // cause false WRONG_ANSWER verdicts.
    private String normalize(String s) {
        if (s == null) return "";
        return s.stripTrailing().lines()
                .map(String::stripTrailing)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }
}
