package com.onlinejudge.problemservice.repository;

import com.onlinejudge.problemservice.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "testCases")
    Optional<Question> findWithTestCasesById(Long id);
}
