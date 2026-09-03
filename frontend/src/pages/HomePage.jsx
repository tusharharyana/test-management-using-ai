import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  const [showInstructions, setShowInstructions] = useState(false);
  const [countdown, setCountdown] = useState(20);

  useEffect(() => {
    if (!showInstructions || countdown === 0) {
      return;
    }

    const timer = setInterval(() => {
      setCountdown((previous) => previous - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [showInstructions, countdown]);

  const handleJoinTest = () => {
    setCountdown(20);
    setShowInstructions(true);
  };

  const handleContinue = () => {
    if (countdown !== 0) {
      return;
    }

    setShowInstructions(false);
    navigate("/student/system-check");
  };

  return (
    <div className="home-page">
      <div className="home-container">
        <div className="brand-badge">AI Powered Code Assessment</div>

        <h1>
          CodeRanBhumi AI
          <span> Evaluation Platform</span>
        </h1>

        <p className="home-description">
          Create coding assessments, let students solve problems using C++,
          Java, or Python, and evaluate their solutions automatically using AI.
        </p>

        <div className="role-cards">
          <div className="role-card student-card">
            <div className="role-icon">{"</>"}</div>

            <h2>I'm a Student</h2>

            <p>
              Enter your test access code and start solving competitive
              programming problems.
            </p>

            <button
              className="primary-button"
              onClick={handleJoinTest}
            >
              Join Test
            </button>
          </div>

          <div className="role-card teacher-card">
            <div className="role-icon">AI</div>

            <h2>I'm a Teacher</h2>

            <p>
              Create coding tests, monitor submissions, review AI evaluations,
              and manage marks.
            </p>

            <button
              className="secondary-button"
              onClick={() => navigate("/teacher")}
            >
              Teacher Dashboard
            </button>
          </div>
        </div>
      </div>

      {/* Test Instructions Modal */}
      {showInstructions && (
        <div className="instructions-overlay">
          <div className="instructions-modal">
            <div className="instructions-icon">⚠</div>

            <h2>Test Instructions</h2>

            <p className="instructions-subtitle">
              Please read all instructions carefully before starting the test.
            </p>

            <div className="instructions-list">
              <div className="instruction-item warning">
                <span>⚠</span>
                <p>
                  Open the test in{" "}
                  <strong>Incognito/Private window</strong> for the best
                  assessment experience.
                </p>
              </div>

              <div className="instruction-item warning">
                <span>⚠</span>
                <p>
                  Do not click <strong>"Submit Test"</strong> until you have
                  completed all questions.
                </p>
              </div>

              <div className="instruction-item success">
                <span>✓</span>
                <p>
                  You may attempt questions in <strong>any order</strong>. Your
                  code is automatically saved while you work.
                </p>
              </div>

              <div className="instruction-item warning">
                <span>⚠</span>
                <p>
                  Do not use <strong>Ctrl+C</strong>,{" "}
                  <strong>Ctrl+P</strong>, the{" "}
                  <strong>Windows/Super key</strong>, or{" "}
                  <strong>Esc</strong> during the test.
                </p>
              </div>

              <div className="instruction-item warning">
                <span>⚠</span>
                <p>
                  <strong>3 security warnings</strong> will result in
                  automatic submission of the test.
                </p>
              </div>
            </div>

            <button
              className="instructions-continue-button"
              disabled={countdown !== 0}
              onClick={handleContinue}
            >
              {countdown > 0
                ? `Continue (${countdown})`
                : "Continue"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default HomePage;