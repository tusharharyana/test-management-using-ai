import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { startTest } from "../../api/attemptApi";

function JoinTestPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    studentName: "",
    studentUid: "",
    accessCode: "",
  });

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    const updatedValue = value.toUpperCase();

    setFormData((previous) => ({
      ...previous,
      [name]: updatedValue,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setLoading(true);
    setError("");

    try {
      const attempt = await startTest(formData);

      // Store attempt temporarily.
      // This lets the TestPage access questions and timer.

      sessionStorage.setItem("currentTestAttempt", JSON.stringify(attempt));

      navigate(`/student/test/${attempt.attemptId}`);
    } catch (error) {
      const message =
        error.response?.data?.message ||
        "Unable to start test. Please try again.";

      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-page">
      <div className="form-card">
        <button className="back-button" onClick={() => navigate("/")}>
          ← Back
        </button>

        <div className="form-header">
          <div className="form-icon">{"</>"}</div>

          <h1>Join Coding Test</h1>

          <p>
            Enter your details and the access code provided by your teacher.
          </p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="studentName">Full Name</label>

            <input
              id="studentName"
              type="text"
              name="studentName"
              value={formData.studentName}
              onChange={handleChange}
              placeholder="Enter your full name"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="studentUid">Student UID</label>

            <input
              id="studentUid"
              type="text"
              name="studentUid"
              value={formData.studentUid}
              onChange={handleChange}
              placeholder="Example: 23BCS001"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="accessCode">Test Access Code</label>

            <input
              id="accessCode"
              type="text"
              name="accessCode"
              value={formData.accessCode}
              onChange={handleChange}
              placeholder="Enter access code"
              required
            />
          </div>

          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? "Starting Test..." : "Start Test"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default JoinTestPage;
