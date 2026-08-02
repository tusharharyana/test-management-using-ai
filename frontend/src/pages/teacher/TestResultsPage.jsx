import { useCallback, useEffect, useMemo, useState } from "react";

import { useNavigate, useParams } from "react-router-dom";

import { getTestResults } from "../../api/resultApi";
import { exportTestResults } from "../../api/exportApi";

function TestResultsPage() {
  const navigate = useNavigate();

  const { testId } = useParams();

  const [results, setResults] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [searchTerm, setSearchTerm] = useState("");

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

  return (
    <div className="teacher-page">
      <header className="teacher-navbar">
        <div className="teacher-brand" onClick={() => navigate("/teacher")}>
          <div className="teacher-brand-icon">{"</>"}</div>

          <div>
            <strong>AI Coding Assessment</strong>

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
