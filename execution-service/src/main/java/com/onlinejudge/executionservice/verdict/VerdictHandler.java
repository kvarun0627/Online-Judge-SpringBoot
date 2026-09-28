package com.onlinejudge.executionservice.verdict;

import com.onlinejudge.executionservice.model.RunOutcome;
import com.onlinejudge.executionservice.model.VerdictStatus;

// Chain of Responsibility: each handler decides ONE concern (TLE? crash? wrong
// output?) and either returns a verdict or delegates to the next handler.
// Adding a new check (e.g. output-size limit) means inserting one new link.
public abstract class VerdictHandler {

    private VerdictHandler next;

    public VerdictHandler setNext(VerdictHandler next) {
        this.next = next;
        return next;
    }

    public final VerdictStatus handle(RunOutcome outcome) {
        VerdictStatus result = evaluate(outcome);
        if (result != null) {
            return result;
        }
        if (next != null) {
            return next.handle(outcome);
        }
        // No handler matched and there's no ACCEPTED handler at the end - misconfiguration.
        return VerdictStatus.INTERNAL_ERROR;
    }

    // Return a verdict if this handler's condition applies, otherwise null to pass along.
    protected abstract VerdictStatus evaluate(RunOutcome outcome);
}
