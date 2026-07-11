import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { activateTest, completeTest, getAllTests } from "../../api/testApi";
import { logoutTeacher } from "../../utils/teacherAuth";

function TeacherDashboard() {
  const navigate = useNavigate();

  const [tests, setTests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [error, setError] = useState("");

  const loadTests = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getAllTests();

      setTests(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Failed to load tests:", error);

      setError(error.response?.data?.message || "Unable to load tests.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTests();
  }, [loadTests]);

  const handleActivate = async (testId) => {
    setActionLoading(`activate-${testId}`);
    setError("");

    try {
      await activateTest(testId);

      await loadTests();
    } catch (error) {
      setError(error.response?.data?.message || "Unable to activate test.");
    } finally {
      setActionLoading(null);
    }
  };

  const handleComplete = async (testId) => {
    const confirmed = window.confirm(
      "Are you sure you want to complete this test? Students will no longer be able to join it.",
    );

    if (!confirmed) {
      return;
    }

    setActionLoading(`complete-${testId}`);
    setError("");

    try {
      await completeTest(testId);

      await loadTests();
    } catch (error) {
      setError(error.response?.data?.message || "Unable to complete test.");
    } finally {
      setActionLoading(null);
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "ACTIVE":
        return "status-active";

      case "COMPLETED":
        return "status-completed";

      default:
        return "status-draft";
    }
  };

  const handleLogout = () => {
    logoutTeacher();

    navigate("/teacher/login", {
      replace: true,
    });
  };

  return (
    <div className="teacher-page">
      <header className="teacher-navbar">
        <div className="teacher-brand" onClick={() => navigate("/")}>
          <div className="teacher-brand-icon">{"</>"}</div>

          <div>
            <strong>AI Coding Assessment</strong>
            <span>Teacher Portal</span>
          </div>
        </div>

        <div className="teacher-navbar-actions">
          <button
            className="create-test-button"
            onClick={() => navigate("/teacher/tests/create")}
          >
            + Create New Test
          </button>

          <button className="teacher-logout-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="teacher-content">
        <section className="dashboard-heading">
          <div>
            <span className="dashboard-eyebrow">Teacher Dashboard</span>

            <h1>Manage Coding Tests</h1>

            <p>
              Create assessments, activate tests, monitor student submissions,
              and review AI-generated evaluations.
            </p>
          </div>
        </section>

        {error && (
          <div className="dashboard-error">
            <span>{error}</span>

            <button onClick={loadTests}>Try Again</button>
          </div>
        )}

        {loading ? (
          <div className="dashboard-state">
            <div className="dashboard-spinner" />
            <p>Loading tests...</p>
          </div>
        ) : tests.length === 0 ? (
          <div className="empty-tests-state">
            <div className="empty-tests-icon">{"</>"}</div>

            <h2>No coding tests yet</h2>

            <p>
              Create your first competitive coding assessment and share its
              access code with students.
            </p>

            <button
              className="create-test-button"
              onClick={() => navigate("/teacher/tests/create")}
            >
              + Create First Test
            </button>
          </div>
        ) : (
          <div className="test-cards-grid">
            {tests.map((test) => (
              <article className="teacher-test-card" key={test.id}>
                <div className="test-card-top">
                  <span
                    className={`test-status-badge ${getStatusClass(
                      test.status,
                    )}`}
                  >
                    {test.status}
                  </span>

                  <span className="test-card-id">#{test.id}</span>
                </div>

                <h2>{test.title}</h2>

                <p className="test-card-description">
                  {test.description || "No description provided."}
                </p>

                <div className="test-card-stats">
                  <div>
                    <span>Duration</span>
                    <strong>{test.durationMinutes} min</strong>
                  </div>

                  <div>
                    <span>Questions</span>
                    <strong>{test.questions?.length || 0}</strong>
                  </div>

                  <div>
                    <span>Access Code</span>
                    <strong className="access-code-value">
                      {test.accessCode || "—"}
                    </strong>
                  </div>
                </div>

                <div className="test-card-actions">
                  {test.status === "DRAFT" && (
                    <button
                      className="activate-test-button"
                      disabled={actionLoading === `activate-${test.id}`}
                      onClick={() => handleActivate(test.id)}
                    >
                      {actionLoading === `activate-${test.id}`
                        ? "Activating..."
                        : "Activate"}
                    </button>
                  )}

                  {test.status === "ACTIVE" && (
                    <button
                      className="complete-test-button"
                      disabled={actionLoading === `complete-${test.id}`}
                      onClick={() => handleComplete(test.id)}
                    >
                      {actionLoading === `complete-${test.id}`
                        ? "Completing..."
                        : "Complete Test"}
                    </button>
                  )}

                  <button
                    className="view-results-button"
                    onClick={() =>
                      navigate(`/teacher/tests/${test.id}/results`)
                    }
                  >
                    View Results
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default TeacherDashboard;
