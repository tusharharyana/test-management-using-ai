package com.example.demo.repository;

import com.example.demo.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.example.demo.enums.SubmissionStatus;

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
    List<Submission> findByStatus(SubmissionStatus status);
    List<Submission> findAllByTestAttempt_Test_Id(Long testId);

}