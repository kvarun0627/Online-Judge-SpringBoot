package com.onlinejudge.problemservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Lob
    @Column(nullable = false)
    private String input;

    @Lob
    @Column(nullable = false, name = "expected_output")
    private String expectedOutput;

    @Column(nullable = false)
    private boolean hidden;
}
