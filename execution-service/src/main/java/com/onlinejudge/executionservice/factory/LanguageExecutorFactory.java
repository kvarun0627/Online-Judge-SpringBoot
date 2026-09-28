package com.onlinejudge.executionservice.factory;

import com.onlinejudge.executionservice.model.Language;
import com.onlinejudge.executionservice.strategy.LanguageExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Factory pattern: Spring injects every LanguageExecutor bean; the factory
// indexes them by language. Adding CppExecutor/JavaExecutor/PythonExecutor
// requires no change here - Open/Closed.
@Component
public class LanguageExecutorFactory {

    private final Map<Language, LanguageExecutor> executorsByLanguage;

    public LanguageExecutorFactory(List<LanguageExecutor> executors) {
        this.executorsByLanguage = executors.stream()
                .collect(Collectors.toMap(LanguageExecutor::supportedLanguage, Function.identity()));
    }

    public LanguageExecutor getExecutor(Language language) {
        LanguageExecutor executor = executorsByLanguage.get(language);
        if (executor == null) {
            throw new IllegalArgumentException("No executor registered for language: " + language);
        }
        return executor;
    }
}
