package com.onlinejudge.executionservice.verdict;

import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;
import org.springframework.stereotype.Component;

@Component
public class RuntimeErrorHandler extends VerdictHandler {
    @Override
    protected VerdictStatus evaluate(RunOutcome outcome) {
        return outcome.isCrashed() ? VerdictStatus.RUNTIME_ERROR : null;
    }
}
