import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import CodeEditor from "../../components/student/CodeEditor";
import QuestionPanel from "../../components/student/QuestionPanel";
import TestTimer from "../../components/student/TestTimer";

import { createSubmission } from "../../api/submissionApi";

import { submitTestAttempt } from "../../api/attemptApi";
import useExamProtection from "../../hooks/useExamProtection";
import ExamViolationModal from "../../components/exam/ExamViolationModal";

const DEFAULT_CODE = {
  CPP: `#include <iostream>
using namespace std;

int main() {

    // Write your solution here

    return 0;
}`,

  JAVA: `import java.util.*;

public class Main {

    public static void main(String[] args) {

        // Write your solution here

    }
}`,

  PYTHON: `# Write your solution here

`,
};

function TestPage() {
  const navigate = useNavigate();

  const { attemptId } = useParams();

  const [attempt, setAttempt] = useState(null);

  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);

  const [language, setLanguage] = useState("CPP");

  const [answers, setAnswers] = useState({});

  const [submitting, setSubmitting] = useState(false);
  const [examEnded, setExamEnded] = useState(false);
  const submittingRef = useRef(false);
  const examEndedRef = useRef(false);

  const [error, setError] = useState("");

  const [showSubmitConfirmation, setShowSubmitConfirmation] = useState(false);
  const {
    warningCount,
    requestFullscreen,
    continueExam,
    showViolationModal,
    violationReason,
    maxWarnings,
  } = useExamProtection({
    maxWarnings: 3,
    isExamActive: !examEnded && !submitting,
    onAutoSubmit: () => submitAllAnswers(true),
  });
  /*
   * Load current test attempt from sessionStorage.
   */
  useEffect(() => {
    if (attempt) {
      requestFullscreen();
    }
  }, [attempt]);
  useEffect(() => {
    const storedAttempt = sessionStorage.getItem("currentTestAttempt");

    if (!storedAttempt) {
      navigate("/student/join");

      return;
    }

    try {
      const parsedAttempt = JSON.parse(storedAttempt);

      if (String(parsedAttempt.attemptId) !== String(attemptId)) {
        navigate("/student/join");

        return;
      }

      setAttempt(parsedAttempt);

      /*
       * Restore previously written code
       * from localStorage.
       */

      const storageKey = `test_answers_${attemptId}`;

      const savedAnswers = localStorage.getItem(storageKey);

      if (savedAnswers) {
        setAnswers(JSON.parse(savedAnswers));
      }
    } catch {
      navigate("/student/join");
    }
  }, [attemptId, navigate]);

  const questions = attempt?.questions || [];

  const currentQuestion = questions[currentQuestionIndex];

  /*
   * Unique key for each question.
   */

  const currentQuestionKey = currentQuestion
    ? String(currentQuestion.id)
    : null;

  /*
   * Get answer for current question.
   */

  const currentAnswer = useMemo(() => {
    if (!currentQuestionKey) {
      return {
        language: "CPP",
        code: DEFAULT_CODE.CPP,
      };
    }

    return (
      answers[currentQuestionKey] || {
        language: "CPP",
        code: DEFAULT_CODE.CPP,
      }
    );
  }, [answers, currentQuestionKey]);

  /*
   * Keep language synchronized
   * with current question.
   */

  useEffect(() => {
    setLanguage(currentAnswer.language || "CPP");
  }, [currentAnswer.language]);

  /*
   * Save answers in localStorage.
   */

  useEffect(() => {
    if (!attempt) {
      return;
    }

    localStorage.setItem(`test_answers_${attemptId}`, JSON.stringify(answers));
  }, [answers, attempt, attemptId]);

  /*
   * Update code.
   */

  const handleCodeChange = (newCode) => {
    if (!currentQuestionKey) {
      return;
    }

    setAnswers((previous) => ({
      ...previous,

      [currentQuestionKey]: {
        language,

        code: newCode,
      },
    }));
  };

  /*
   * Change programming language.
   */

  const handleLanguageChange = (event) => {
    const newLanguage = event.target.value;

    setLanguage(newLanguage);

    setAnswers((previous) => {
      const previousAnswer = previous[currentQuestionKey];

      return {
        ...previous,

        [currentQuestionKey]: {
          language: newLanguage,

          code:
            previousAnswer?.language === newLanguage
              ? previousAnswer.code
              : DEFAULT_CODE[newLanguage],
        },
      };
    });
  };

  /*
   * Change question.
   */

  const goToQuestion = (index) => {
    if (index >= 0 && index < questions.length) {
      setCurrentQuestionIndex(index);
    }
  };

  /*
   * Submit all answers.
   */

  const submitAllAnswers = useCallback(
    async (autoSubmit = false) => {
      if (!attempt) {
        return;
      }

      // Prevent duplicate submissions
      if (submittingRef.current) {
        return;
      }

      submittingRef.current = true;

      setSubmitting(true);

      setError("");

      setShowSubmitConfirmation(false);

      try {
        const submissionIds = [];

        /*
         * Submit each answered question.
         */

        for (const question of questions) {
          const questionKey = String(question.id);

          const answer = answers[questionKey];

          /*
           * Skip unanswered questions.
           */

          if (!answer || !answer.code || !answer.code.trim()) {
            continue;
          }

          const submission = await createSubmission({
            attemptId: Number(attempt.attemptId),

            questionId: Number(question.id),

            language: answer.language,

            sourceCode: answer.code,
          });

          submissionIds.push(submission.submissionId);
        }

        /*
         * Mark overall attempt as submitted.
         */

        await submitTestAttempt(Number(attempt.attemptId));

        /*
         * Save basic success information.
         */

        sessionStorage.setItem(
          "lastSubmissionResult",
          JSON.stringify({
            attemptId: attempt.attemptId,

            testTitle: attempt.testTitle,

            submissionIds,

            autoSubmitted: autoSubmit,
          }),
        );

        /*
         * Remove current attempt data.
         */

        sessionStorage.removeItem("currentTestAttempt");

        localStorage.removeItem(`test_answers_${attemptId}`);

        navigate("/student/success", {
          replace: true,
        });
      } catch (error) {
        console.error("Submission failed:", error);

        const message =
          error.response?.data?.message ||
          "Unable to submit test. Please try again.";

        setError(message);

        submittingRef.current = false;

        setSubmitting(false);
      }
    },
    [submitting, attempt, questions, answers, attemptId, navigate],
  );

  /*
   * Automatic submission when timer expires.
   */

  const handleTimeUp = useCallback(() => {
    // Already ended
    if (examEndedRef.current) {
      return;
    }

    // Mark exam as ended immediately
    examEndedRef.current = true;

    setExamEnded(true);

    // Automatically submit
    submitAllAnswers(true);
  }, [submitAllAnswers]);

  if (!attempt) {
    return <div className="test-loading-page">Loading test...</div>;
  }

  if (questions.length === 0) {
    return (
      <div className="test-loading-page">No questions found for this test.</div>
    );
  }

  return (
    <div className="coding-test-page">
      {/* Header */}

      <header className="test-header">
        <div className="test-brand">
          <div className="test-brand-icon">{"</>"}</div>

          <div>
            <strong>AI Coding Assessment</strong>

            <span>{attempt.studentName}</span>
          </div>
        </div>

        <div className="test-title-header">{attempt.testTitle}</div>

        <div className="exam-header-right">
          <div className="warning-box">⚠ Warnings: {warningCount}/3</div>

          <TestTimer expiresAt={attempt.expiresAt} onTimeUp={handleTimeUp} />
        </div>
      </header>

      {/* Main content */}

      <main className="test-workspace">
        {/* Question area */}

        <aside className="problem-area">
          <QuestionPanel
            question={currentQuestion}
            currentIndex={currentQuestionIndex}
            totalQuestions={questions.length}
          />
        </aside>

        {/* Editor area */}

        <section className="editor-area">
          <div className="editor-toolbar">
            <div className="question-navigation">
              {questions.map((question, index) => (
                <button
                  key={question.id}
                  className={
                    currentQuestionIndex === index
                      ? "question-nav-button active"
                      : "question-nav-button"
                  }
                  onClick={() => goToQuestion(index)}
                >
                  Q{index + 1}
                  {answers[String(question.id)]?.code?.trim() && (
                    <span className="answered-dot" />
                  )}
                </button>
              ))}
            </div>

            <select
              className="language-selector"
              value={language}
              onChange={handleLanguageChange}
            >
              <option value="CPP">C++</option>

              <option value="JAVA">Java</option>

              <option value="PYTHON">Python</option>
            </select>
          </div>

          <div className="editor-container">
            <CodeEditor
              language={language}
              code={currentAnswer.code}
              onChange={handleCodeChange}
            />
          </div>

          <div className="test-footer">
            <div className="question-controls">
              <button
                className="navigation-button"
                disabled={currentQuestionIndex === 0}
                onClick={() => goToQuestion(currentQuestionIndex - 1)}
              >
                ← Previous
              </button>

              <button
                className="navigation-button"
                disabled={currentQuestionIndex === questions.length - 1}
                onClick={() => goToQuestion(currentQuestionIndex + 1)}
              >
                Next →
              </button>
            </div>

            <button
              className="final-submit-button"
              disabled={submitting}
              onClick={() => setShowSubmitConfirmation(true)}
            >
              {submitting ? "Submitting..." : "Submit Test"}
            </button>
          </div>

          {error && <div className="test-error-message">{error}</div>}
        </section>
      </main>

      {/* Confirmation modal */}

      {showSubmitConfirmation && (
        <div className="modal-overlay">
          <div className="confirmation-modal">
            <div className="modal-icon">✓</div>

            <h2>Submit your test?</h2>

            <p>Once submitted, you cannot change your answers.</p>

            <div className="submission-summary">
              <span>Answered</span>

              <strong>
                {
                  questions.filter((question) =>
                    answers[String(question.id)]?.code?.trim(),
                  ).length
                }

                {" / "}

                {questions.length}
              </strong>
            </div>

            <div className="modal-actions">
              <button
                className="cancel-submit-button"
                onClick={() => setShowSubmitConfirmation(false)}
              >
                Continue Coding
              </button>

              <button
                className="confirm-submit-button"
                onClick={() => submitAllAnswers(false)}
                disabled={submitting}
              >
                {submitting ? "Submitting..." : "Yes, Submit Test"}
              </button>
            </div>
          </div>
        </div>
      )}
      <ExamViolationModal
        open={showViolationModal}
        reason={violationReason}
        warningCount={warningCount}
        maxWarnings={maxWarnings}
        onContinue={continueExam}
      />
    </div>
  );
}

export default TestPage;
