import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function SystemCheckPage() {
  const navigate = useNavigate();

  const [checks, setChecks] = useState({
    internet: {
      status: "checking",
      message: "Checking internet connection...",
    },
    storage: {
      status: "checking",
      message: "Checking browser storage...",
    },
    fullscreen: {
      status: "checking",
      message: "Checking fullscreen support...",
    },
  });

  const [checking, setChecking] = useState(true);

  const [allPassed, setAllPassed] = useState(false);

  const updateCheck = (name, status, message) => {
    setChecks((previous) => ({
      ...previous,
      [name]: {
        status,
        message,
      },
    }));
  };

  const runChecks = async () => {
    setChecking(true);
    setAllPassed(false);

    // =========================
    // 1. Internet Connection
    // =========================

    const internetPassed = navigator.onLine;

    if (internetPassed) {
      updateCheck("internet", "passed", "Internet connection is available");
    } else {
      updateCheck("internet", "failed", "Internet connection is unavailable");
    }

    // =========================
    // 2. Local Storage
    // =========================

    let storagePassed = false;

    try {
      const testKey = "__system_check_test__";

      localStorage.setItem(testKey, "ok");

      const value = localStorage.getItem(testKey);

      localStorage.removeItem(testKey);

      if (value === "ok") {
        storagePassed = true;

        updateCheck("storage", "passed", "Browser storage is available");
      } else {
        updateCheck("storage", "failed", "Browser storage is not working");
      }
    } catch (error) {
      updateCheck("storage", "failed", "Browser storage is blocked");
    }

    // =========================
    // 3. Fullscreen Support
    // =========================

    const fullscreenPassed = document.fullscreenEnabled;

    if (fullscreenPassed) {
      updateCheck("fullscreen", "passed", "Fullscreen mode is supported");
    } else {
      updateCheck("fullscreen", "failed", "Fullscreen mode is not supported");
    }

    // =========================
    // FINAL RESULT
    // =========================

    const passed = internetPassed && storagePassed && fullscreenPassed;

    setAllPassed(passed);

    setChecking(false);

    return passed;
  };

  useEffect(() => {
    runChecks();
  }, []);

  useEffect(() => {
    if (checking) {
      return;
    }

    const passed = Object.values(checks).every(
      (check) => check.status === "passed",
    );

    setAllPassed(passed);
  }, [checks, checking]);

  const handleContinue = async () => {
    // Run one final system check
    const passed = await runChecks();

    // If the final check fails, stay on this page
    if (!passed) {
      return;
    }

    // Remember that the student successfully
    // completed the final system check.
    sessionStorage.setItem("systemCheckPassed", "true");

    // No test attempt is created here.
    // No timer starts here.
    // Move to the access-code page.
    navigate("/student/join");
  };

  const renderIcon = (status) => {
    if (status === "checking") {
      return "⏳";
    }

    if (status === "passed") {
      return "✓";
    }

    return "✕";
  };

  const renderCheck = (name, title) => {
    const check = checks[name];

    return (
      <div className={`system-check-item ${check.status}`}>
        <div className="system-check-icon">{renderIcon(check.status)}</div>

        <div className="system-check-content">
          <strong>{title}</strong>

          <span>{check.message}</span>
        </div>
      </div>
    );
  };

  return (
    <div className="system-check-page">
      {/* Header */}
      <header className="system-check-navbar">
        <div className="system-check-brand">
          <div className="system-check-brand-icon">{"</>"}</div>

          <div className="system-check-brand-text">
            <strong>CodeRanBhumi - AI Coding Assessment</strong>
            <span>System Check</span>
          </div>
        </div>
      </header>

      <main className="system-check-container">
        {/* Page Heading */}
        <div className="system-check-heading">
          <span className="system-check-eyebrow">Pre-Assessment Check</span>

          <h1>System Check</h1>

          <p>
            Let's make sure your device is ready before you join the coding
            assessment.
          </p>
        </div>

        {/* Main Card */}
        <div className="system-check-card">
          {/* Progress */}
          <div className="system-check-progress">
            <div className="progress-header">
              <span>System readiness</span>

              <span>
                {
                  Object.values(checks).filter(
                    (check) => check.status === "passed",
                  ).length
                }{" "}
                / {Object.keys(checks).length}
              </span>
            </div>

            <div className="progress-track">
              <div
                className="progress-fill"
                style={{
                  width: `${
                    (Object.values(checks).filter(
                      (check) => check.status === "passed",
                    ).length /
                      Object.keys(checks).length) *
                    100
                  }%`,
                }}
              />
            </div>
          </div>

          {/* Checks */}
          <div className="system-check-list">
            {renderCheck("internet", "Internet Connection")}

            {renderCheck("storage", "Browser Storage")}

            {renderCheck("fullscreen", "Fullscreen Support")}
          </div>

          {/* Success */}
          {allPassed && (
            <div className="system-check-success">
              <div className="success-icon">✓</div>

              <div className="success-content">
                <strong>System Ready</strong>

                <span>Your device is ready for the assessment.</span>
              </div>
            </div>
          )}

          {/* Error */}
          {!checking && !allPassed && (
            <div className="system-check-error">
              <div className="error-icon">!</div>

              <div>
                <strong>Action Required</strong>

                <span>
                  One or more checks failed. Fix the issue and try again.
                </span>
              </div>
            </div>
          )}

          {/* Buttons */}
          <div className="system-check-actions">
            {!allPassed && (
              <button
                className="system-check-retry"
                onClick={runChecks}
                disabled={checking}
              >
                {checking ? (
                  <>
                    <span className="button-spinner"></span>
                    Checking...
                  </>
                ) : (
                  <>↻ Check Again</>
                )}
              </button>
            )}

            <button
              className="system-check-start"
              onClick={handleContinue}
              disabled={!allPassed || checking}
            >
              Continue
              <span>→</span>
            </button>
          </div>

          {/* Footer */}
          <div className="system-check-footer">
            <span>🔒 Secure Assessment Environment</span>

            <span>•</span>

            <span>3 checks required</span>
          </div>
        </div>
      </main>
    </div>
  );
}

export default SystemCheckPage;
