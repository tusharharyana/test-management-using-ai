import { useCallback, useEffect, useMemo, useState } from "react";

import { useNavigate, useParams } from "react-router-dom";

import {
  getTestResults,
  reEvaluateTest,
  reEvaluateStudent,
} from "../../api/resultApi";
import { exportTestResults } from "../../api/exportApi";
import { deleteAttempt } from "../../api/attemptApi";

function TestResultsPage() {
  const navigate = useNavigate();

  const { testId } = useParams();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [deletingAttemptId, setDeletingAttemptId] = useState(null);
  const [reEvaluatingTest, setReEvaluatingTest] = useState(false);
  const [reEvaluatingAttemptId, setReEvaluatingAttemptId] = useState(null);

  const loadResults = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getTestResults(testId);

      setResults(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Failed to load test results:", error);

      setError(error.response?.data?.message || "Unable to load test results.");
    } finally {
      setLoading(false);
    }
  }, [testId]);

  useEffect(() => {
    loadResults();
  }, [loadResults]);

  const filteredResults = useMemo(() => {
    const search = searchTerm.trim().toLowerCase();

    if (!search) {
      return results;
    }

    return results.filter((result) => {
      const name = result.studentName?.toLowerCase() || "";

      const uid = result.studentUid?.toLowerCase() || "";

      return name.includes(search) || uid.includes(search);
    });
  }, [results, searchTerm]);

  const statistics = useMemo(() => {
    const totalStudents = results.length;

    const submittedStudents = results.filter(
      (result) => result.attemptStatus === "SUBMITTED",
    ).length;

    const evaluatedStudents = results.filter((result) =>
      result.submissions?.some((submission) => submission.evaluation !== null),
    ).length;

    const scores = results
      .map((result) => result.finalTotalScore)
      .filter((score) => typeof score === "number");

    const averageScore =
      scores.length > 0
        ? scores.reduce((sum, score) => sum + score, 0) / scores.length
        : 0;

    return {
      totalStudents,
      submittedStudents,
      evaluatedStudents,
      averageScore,
    };
  }, [results]);

  const getAttemptStatusClass = (status) => {
    switch (status) {
      case "SUBMITTED":
        return "result-status-submitted";

      case "IN_PROGRESS":
        return "result-status-progress";

      case "EXPIRED":
        return "result-status-expired";

      default:
        return "result-status-default";
    }
  };

  const handleDeleteAttempt = async (result) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete the attempt of ${result.studentName} (${result.studentUid})?\n\nAfter deletion, this student will be able to attempt the test again.`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingAttemptId(result.attemptId);

      await deleteAttempt(result.attemptId);

      // Remove deleted attempt from current table
      setResults((previousResults) =>
        previousResults.filter((item) => item.attemptId !== result.attemptId),
      );
    } catch (error) {
      console.error("Failed to delete attempt:", error);

      alert(
        error.response?.data?.message || "Failed to delete student attempt.",
      );
    } finally {
      setDeletingAttemptId(null);
    }
  };

  const handleExport = async () => {
    try {
      const blob = await exportTestResults(testId);

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");

      link.href = url;

      link.download = `Test_${testId}_Results.xlsx`;

      document.body.appendChild(link);

      link.click();

      link.remove();

      window.URL.revokeObjectURL(url);
    } catch (error) {
      alert("Failed to export Excel.");
    }
  };

  const handleReEvaluateTest = async () => {
    const confirmed = window.confirm(
      "Are you sure you want to re-evaluate all student submissions using AI?\n\n" +
      "This will send all submissions for this test back to the AI evaluation queue.",
    );

    if (!confirmed) {
      return;
    }

    try {
      setReEvaluatingTest(true);

      await reEvaluateTest(testId);

      alert(
        "AI re-evaluation has been started for all student submissions.",
      );

      await loadResults();
    } catch (error) {
      console.error("Failed to start AI re-evaluation:", error);

      alert(
        error.response?.data?.message ||
        "Failed to start AI re-evaluation.",
      );
    } finally {
      setReEvaluatingTest(false);
    }
  };

  const handleReEvaluateStudent = async (result) => {
    const confirmed = window.confirm(
      `Are you sure you want to re-evaluate all submissions of ${result.studentName} (${result.studentUid}) using AI?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setReEvaluatingAttemptId(result.attemptId);

      await reEvaluateStudent(testId, result.attemptId);

      alert(
        `AI re-evaluation has been started for ${result.studentName}.`,
      );

      await loadResults();
    } catch (error) {
      console.error("Failed to start student AI re-evaluation:", error);

      alert(
        error.response?.data?.message ||
        "Failed to start AI re-evaluation for this student.",
      );
    } finally {
      setReEvaluatingAttemptId(null);
    }
  };

  return (
    <div className="teacher-page">
      <header className="teacher-navbar">
        <div className="teacher-brand" onClick={() => navigate("/teacher")}>
          <div className="teacher-brand-icon">{"</>"}</div>

          <div>
            <strong>CodeRanBhumi - AI Coding Assessment</strong>

            <span>Test Results</span>
          </div>
        </div>

        <button
          className="results-refresh-button"
          onClick={loadResults}
          disabled={loading}
        >
          ↻ Refresh
        </button>
      </header>

      <main className="teacher-content">
        <button
          className="teacher-back-button"
          onClick={() => navigate("/teacher")}
        >
          ← Back to Dashboard
        </button>

        <div className="results-page-heading">
          <div>
            <span className="dashboard-eyebrow">Test #{testId}</span>

            <h1>Student Results</h1>

            <p>
              Review AI evaluations, student scores, confidence levels, and
              manually override marks when necessary.
            </p>
          </div>
        </div>

        {error && (
          <div className="dashboard-error">
            <span>{error}</span>

            <button onClick={loadResults}>Try Again</button>
          </div>
        )}

        {!loading && (
          <div className="result-statistics-grid">
            <div className="result-stat-card">
              <span>Total Students</span>

              <strong>{statistics.totalStudents}</strong>
            </div>

            <div className="result-stat-card">
              <span>Submitted</span>

              <strong>{statistics.submittedStudents}</strong>
            </div>

            <div className="result-stat-card">
              <span>AI Evaluated</span>

              <strong>{statistics.evaluatedStudents}</strong>
            </div>

            <div className="result-stat-card">
              <span>Average Score</span>

              <strong>{statistics.averageScore.toFixed(1)}</strong>
            </div>
          </div>
        )}

        <section className="results-table-section">
          <div className="results-table-header">
            <div>
              <h2>Student Attempts</h2>

              <p>
                {results.length} total attempt
                {results.length !== 1 ? "s" : ""}
              </p>
            </div>

            <input
              className="results-search-input"
              type="text"
              placeholder="Search by name or UID..."
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
            />

            <button
              onClick={handleReEvaluateTest}
              className="ai-reevaluate-test-button"
              disabled={reEvaluatingTest || loading || results.length === 0}
            >
              {reEvaluatingTest ? "Starting AI Re-evaluation..." : "AI Re-evaluate All"}
            </button>

            <button onClick={handleExport} className="export-excel-button">
              Export Excel
            </button>
          </div>

          {loading ? (
            <div className="dashboard-state">
              <div className="dashboard-spinner" />

              <p>Loading student results...</p>
            </div>
          ) : filteredResults.length === 0 ? (
            <div className="results-empty-state">
              <div>📋</div>

              <h3>No student results found</h3>

              <p>
                {searchTerm
                  ? "No students match your search."
                  : "No students have attempted this test yet."}
              </p>
            </div>
          ) : (
            <div className="results-table-wrapper">
              <table className="results-table">
                <thead>
                  <tr>
                    <th>Student</th>

                    <th>UID</th>

                    <th>Status</th>

                    <th>AI Score</th>

                    <th>Final Score</th>

                    <th>Maximum</th>

                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredResults.map((result) => (
                    <tr key={result.attemptId}>
                      <td>
                        <div className="student-table-name">
                          <div className="student-avatar">
                            {result.studentName?.charAt(0)?.toUpperCase() ||
                              "S"}
                          </div>

                          <strong>{result.studentName}</strong>
                        </div>
                      </td>

                      <td>
                        <span className="student-uid">{result.studentUid}</span>
                      </td>

                      <td>
                        <span
                          className={`result-status-badge ${getAttemptStatusClass(
                            result.attemptStatus,
                          )}`}
                        >
                          {result.attemptStatus}
                        </span>
                      </td>

                      <td>
                        <strong>{result.aiTotalScore ?? 0}</strong>
                      </td>

                      <td>
                        <strong className="final-score-value">
                          {result.finalTotalScore ?? 0}
                        </strong>
                      </td>

                      <td>{result.maximumPossibleScore}</td>

                      <td>
                        <div className="student-result-actions">
                          <button
                            className="view-student-result-button"
                            onClick={() =>
                              navigate(
                                `/teacher/tests/${testId}/results/${result.attemptId}`,
                              )
                            }
                          >
                            View Details
                          </button>

                          <button
                            className="ai-reevaluate-student-button"
                            onClick={() => handleReEvaluateStudent(result)}
                            disabled={reEvaluatingAttemptId === result.attemptId}
                          >
                            {reEvaluatingAttemptId === result.attemptId
                              ? "Re-evaluating..."
                              : "AI Re-evaluate"}
                          </button>

                          <button
                            className="delete-attempt-button"
                            onClick={() => handleDeleteAttempt(result)}
                            disabled={deletingAttemptId === result.attemptId}
                          >
                            {deletingAttemptId === result.attemptId
                              ? "Deleting..."
                              : "Delete Attempt"}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default TestResultsPage;
