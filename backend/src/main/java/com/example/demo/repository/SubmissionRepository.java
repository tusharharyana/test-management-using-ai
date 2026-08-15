package com.example.demo.repository;

import com.example.demo.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    boolean existsByTestAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    Optional<Submission> findByTestAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    List<Submission> findAllByTestAttemptId(Long attemptId);
    void deleteByTestAttemptId(Long attemptId);
}