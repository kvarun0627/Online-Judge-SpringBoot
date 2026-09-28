package com.onlinejudge.executionservice.verdict;

import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;
import org.springframework.stereotype.Component;

// Terminal link in the chain: if nothing else matched, it's Accepted.
@Component
public class AcceptedHandler extends VerdictHandler {
    @Override
    protected VerdictStatus evaluate(RunOutcome outcome) {
        return VerdictStatus.ACCEPTED;
    }
}
