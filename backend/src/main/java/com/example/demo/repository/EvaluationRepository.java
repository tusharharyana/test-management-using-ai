package com.example.demo.repository;

import com.example.demo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository
        extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findBySubmissionId(Long submissionId);

    boolean existsBySubmissionId(Long submissionId);

    List<Evaluation> findBySubmission_TestAttempt_Test_Id(Long testId);
}