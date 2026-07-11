package com.example.demo.repository;

import com.example.demo.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestAttemptRepository
        extends JpaRepository<TestAttempt, Long> {

    boolean existsByTestIdAndStudentUid(
            Long testId,
            String studentUid
    );

    Optional<TestAttempt> findByTestIdAndStudentUid(
            Long testId,
            String studentUid
    );

    List<TestAttempt> findAllByTestId(Long testId);
}