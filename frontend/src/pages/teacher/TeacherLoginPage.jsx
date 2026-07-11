import { useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";

import { isTeacherAuthenticated, loginTeacher } from "../../utils/teacherAuth";

function TeacherLoginPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const [teacherId, setTeacherId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  /*
   * If teacher originally tried to visit a protected page,
   * return there after successful login.
   */
  const destination = location.state?.from || "/teacher";

  /*
   * If already logged in, no need to show login page.
   */
  if (isTeacherAuthenticated()) {
    return <Navigate to={destination} replace />;
  }

  const handleSubmit = (event) => {
    event.preventDefault();

    setError("");

    const success = loginTeacher(teacherId.trim(), password);

    if (!success) {
      setError("Invalid teacher ID or password.");
      return;
    }

    navigate(destination, {
      replace: true,
    });
  };

  return (
    <div className="teacher-login-page">
      <div className="teacher-login-card">
        <div className="teacher-login-logo">{"</>"}</div>

        <span className="dashboard-eyebrow">Teacher Portal</span>

        <h1>Welcome Back</h1>

        <p className="teacher-login-description">
          Sign in to manage coding tests, student submissions, and AI
          evaluations.
        </p>

        {error && <div className="teacher-login-error">{error}</div>}

        <form className="teacher-login-form" onSubmit={handleSubmit}>
          <div className="teacher-login-group">
            <label htmlFor="teacherId">Teacher ID</label>

            <input
              id="teacherId"
              type="text"
              value={teacherId}
              onChange={(event) => setTeacherId(event.target.value)}
              placeholder="Enter teacher ID"
              autoComplete="username"
              required
              autoFocus
            />
          </div>

          <div className="teacher-login-group">
            <label htmlFor="teacherPassword">Password</label>

            <input
              id="teacherPassword"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Enter password"
              autoComplete="current-password"
              required
            />
          </div>

          <button type="submit" className="teacher-login-button">
            Sign In to Dashboard
          </button>
        </form>

        <button
          type="button"
          className="teacher-login-home-button"
          onClick={() => navigate("/")}
        >
          ← Return to Home
        </button>
      </div>
    </div>
  );
}

export default TeacherLoginPage;
