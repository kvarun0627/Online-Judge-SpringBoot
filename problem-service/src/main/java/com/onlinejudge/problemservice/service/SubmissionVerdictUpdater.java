package com.onlinejudge.problemservice.service;

import com.onlinejudge.problemservice.client.ExecutionResult;

// Second, narrow interface on the submission side - same ISP reasoning as
// QuestionCommandService/QuestionQueryService. Only VerdictStreamListener
// depends on this; SubmissionController never sees it.
public interface SubmissionVerdictUpdater {
    void applyVerdict(ExecutionResult result);
}
