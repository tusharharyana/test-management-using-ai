import { useCallback, useEffect, useState } from "react";

import { useNavigate, useParams } from "react-router-dom";

import { getStudentResult, overrideMarks } from "../../api/resultApi";

function StudentResultPage() {
  const navigate = useNavigate();

  const { testId, attemptId } = useParams();

  const [result, setResult] = useState(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [editingEvaluationId, setEditingEvaluationId] = useState(null);

  const [overrideForm, setOverrideForm] = useState({
    teacherScore: "",
    teacherComment: "",
  });

  const [savingOverride, setSavingOverride] = useState(false);

  const loadStudentResult = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getStudentResult(testId, attemptId);

      setResult(data);
    } catch (error) {
      console.error("Failed to load student result:", error);

      setError(
        error.response?.data?.message || "Unable to load student result.",
      );
    } finally {
      setLoading(false);
    }
  }, [testId, attemptId]);

  useEffect(() => {
    loadStudentResult();
  }, [loadStudentResult]);

  const openOverrideEditor = (evaluation) => {
    setEditingEvaluationId(evaluation.evaluationId);

    setOverrideForm({
      teacherScore: evaluation.teacherScore ?? evaluation.finalScore ?? "",

      teacherComment: evaluation.teacherComment || "",
    });
  };

  const closeOverrideEditor = () => {
    setEditingEvaluationId(null);

    setOverrideForm({
      teacherScore: "",
      teacherComment: "",
    });
  };

  const handleOverrideSave = async (evaluationId) => {
    setSavingOverride(true);
    setError("");

    try {
      await overrideMarks(evaluationId, {
        teacherScore: Number(overrideForm.teacherScore),

        teacherComment: overrideForm.teacherComment,
      });

      closeOverrideEditor();

      await loadStudentResult();
    } catch (error) {
      console.error("Failed to override marks:", error);

      setError(error.response?.data?.message || "Unable to update marks.");
    } finally {
      setSavingOverride(false);
    }
  };

  const getConfidenceClass = (confidence) => {
    if (confidence >= 85) {
      return "confidence-high";
    }

    if (confidence >= 60) {
      return "confidence-medium";
    }

    return "confidence-low";
  };

  if (loading) {
    return (
      <div className="teacher-page">
        <div className="dashboard-state full-page-state">
          <div className="dashboard-spinner" />

          <p>Loading student evaluation...</p>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="teacher-page">
        <div className="dashboard-state full-page-state">
          <p>Student result not found.</p>

          <button
            className="view-results-button"
            onClick={() => navigate(`/teacher/tests/${testId}/results`)}
          >
            Return to Results
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="teacher-page">
      <header className="teacher-navbar">
        <div className="teacher-brand" onClick={() => navigate("/teacher")}>
          <div className="teacher-brand-icon">{"</>"}</div>

          <div>
            <strong>AI Coding Assessment</strong>

            <span>Student Evaluation</span>
          </div>
        </div>
      </header>

      <main className="teacher-content">
        <button
          className="teacher-back-button"
          onClick={() => navigate(`/teacher/tests/${testId}/results`)}
        >
          ← Back to Test Results
        </button>

        {error && (
          <div className="dashboard-error">
            <span>{error}</span>
          </div>
        )}

        <section className="student-result-hero">
          <div className="student-result-identity">
            <div className="large-student-avatar">
              {result.studentName?.charAt(0)?.toUpperCase() || "S"}
            </div>

            <div>
              <span className="dashboard-eyebrow">Student Evaluation</span>

              <h1>{result.studentName}</h1>

              <p>
                UID: {result.studentUid}
                {" · "}
                Attempt #{result.attemptId}
              </p>
            </div>
          </div>

          <div className="student-score-summary">
            <div>
              <span>AI Score</span>

              <strong>{result.aiTotalScore ?? 0}</strong>
            </div>

            <div>
              <span>Final Score</span>

              <strong className="final-score-value">
                {result.finalTotalScore ?? 0}
              </strong>
            </div>

            <div>
              <span>Maximum</span>

              <strong>{result.maximumPossibleScore}</strong>
            </div>
          </div>
        </section>

        <section className="evaluation-list-section">
          <div className="evaluation-list-heading">
            <h2>Question Evaluations</h2>

            <p>
              AI-generated scoring breakdown, confidence, feedback, and teacher
              review.
            </p>
          </div>

          <div className="evaluation-cards-list">
            {result.submissions?.length > 0 ? (
              result.submissions.map((submission, index) => {
                const evaluation = submission.evaluation;

                return (
                  <article
                    className="evaluation-card"
                    key={submission.submissionId}
                  >
                    <div className="evaluation-card-header">
                      <div>
                        <span className="question-label">
                          Question {index + 1}
                        </span>

                        <h3>{submission.questionTitle}</h3>
                      </div>

                      <div className="evaluation-score-display">
                        {evaluation ? (
                          <>
                            <strong>{evaluation.finalScore}</strong>

                            <span>marks</span>
                          </>
                        ) : (
                          <span className="evaluation-pending">Pending</span>
                        )}
                      </div>
                    </div>

                    <div className="submission-meta-row">
                      <span>
                        Language: <strong>{submission.language}</strong>
                      </span>

                      <span>
                        Status: <strong>{submission.status}</strong>
                      </span>
                    </div>

                    {!evaluation ? (
                      <div className="evaluation-waiting">
                        AI evaluation is still pending.
                      </div>
                    ) : (
                      <>
                        <div className="score-breakdown-grid">
                          <div>
                            <span>Correctness</span>

                            <strong>{evaluation.correctnessScore}</strong>
                          </div>

                          <div>
                            <span>Edge Cases</span>

                            <strong>{evaluation.edgeCaseScore}</strong>
                          </div>

                          <div>
                            <span>Efficiency</span>

                            <strong>{evaluation.efficiencyScore}</strong>
                          </div>

                          <div>
                            <span>Code Quality</span>

                            <strong>{evaluation.codeQualityScore}</strong>
                          </div>

                          <div>
                            <span>Syntax</span>

                            <strong>{evaluation.syntaxScore}</strong>
                          </div>
                        </div>

                        <div className="evaluation-details-grid">
                          <div className="ai-feedback-box">
                            <h4>AI Feedback</h4>

                            <p>
                              {evaluation.feedback || "No feedback provided."}
                            </p>
                          </div>

                          <div className="confidence-box">
                            <span>AI Confidence</span>

                            <strong
                              className={getConfidenceClass(
                                evaluation.confidence,
                              )}
                            >
                              {evaluation.confidence}%
                            </strong>
                          </div>
                        </div>

                        <div className="score-comparison-row">
                          <div>
                            <span>AI Score</span>

                            <strong>{evaluation.aiScore}</strong>
                          </div>

                          <div>
                            <span>Teacher Score</span>

                            <strong>
                              {evaluation.teacherScore ?? "Not overridden"}
                            </strong>
                          </div>

                          <div>
                            <span>Final Score</span>

                            <strong className="final-score-value">
                              {evaluation.finalScore}
                            </strong>
                          </div>
                        </div>

                        {evaluation.teacherComment && (
                          <div className="teacher-comment-box">
                            <strong>Teacher Comment</strong>

                            <p>{evaluation.teacherComment}</p>
                          </div>
                        )}

                        {editingEvaluationId === evaluation.evaluationId ? (
                          <div className="override-editor">
                            <div className="override-editor-heading">
                              <h4>Override AI Marks</h4>

                              <p>
                                Enter your final score and an optional comment.
                              </p>
                            </div>

                            <div className="override-form-grid">
                              <div className="teacher-form-group">
                                <label>Teacher Score</label>

                                <input
                                  type="number"
                                  min="0"
                                  value={overrideForm.teacherScore}
                                  onChange={(event) =>
                                    setOverrideForm((previous) => ({
                                      ...previous,

                                      teacherScore: event.target.value,
                                    }))
                                  }
                                />
                              </div>

                              <div className="teacher-form-group full-width">
                                <label>Teacher Comment</label>

                                <textarea
                                  rows="3"
                                  value={overrideForm.teacherComment}
                                  onChange={(event) =>
                                    setOverrideForm((previous) => ({
                                      ...previous,

                                      teacherComment: event.target.value,
                                    }))
                                  }
                                  placeholder="Optional comment about this override..."
                                />
                              </div>
                            </div>

                            <div className="override-actions">
                              <button
                                className="cancel-create-button"
                                onClick={closeOverrideEditor}
                                disabled={savingOverride}
                              >
                                Cancel
                              </button>

                              <button
                                className="save-test-button"
                                onClick={() =>
                                  handleOverrideSave(evaluation.evaluationId)
                                }
                                disabled={
                                  savingOverride ||
                                  overrideForm.teacherScore === ""
                                }
                              >
                                {savingOverride ? "Saving..." : "Save Override"}
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="evaluation-actions">
                            <button
                              className="override-marks-button"
                              onClick={() => openOverrideEditor(evaluation)}
                            >
                              Override Marks
                            </button>
                          </div>
                        )}
                      </>
                    )}
                  </article>
                );
              })
            ) : (
              <div className="results-empty-state">
                <h3>No submissions found</h3>

                <p>This student has not submitted any coding solutions.</p>
              </div>
            )}
          </div>
        </section>
      </main>
    </div>
  );
}

export default StudentResultPage;
