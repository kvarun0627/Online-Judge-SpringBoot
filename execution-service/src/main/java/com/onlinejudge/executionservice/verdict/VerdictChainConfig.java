package com.onlinejudge.executionservice.verdict;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VerdictChainConfig {

    // Order matters: TLE and crashes are checked before comparing output.
    @Bean
    public VerdictHandler verdictChain(TimeLimitHandler timeLimitHandler,
                                        RuntimeErrorHandler runtimeErrorHandler,
                                        WrongAnswerHandler wrongAnswerHandler,
                                        AcceptedHandler acceptedHandler) {
        timeLimitHandler.setNext(runtimeErrorHandler)
                .setNext(wrongAnswerHandler)
                .setNext(acceptedHandler);
        return timeLimitHandler;
    }
}
