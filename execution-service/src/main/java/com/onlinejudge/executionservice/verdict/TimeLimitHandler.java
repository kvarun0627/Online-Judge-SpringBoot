package com.onlinejudge.executionservice.verdict;

import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;
import org.springframework.stereotype.Component;

@Component
public class TimeLimitHandler extends VerdictHandler {
    @Override
    protected VerdictStatus evaluate(RunOutcome outcome) {
        return outcome.isTimedOut() ? VerdictStatus.TIME_LIMIT_EXCEEDED : null;
    }
}
