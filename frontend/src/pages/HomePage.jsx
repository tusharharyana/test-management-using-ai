import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="home-page">
      <div className="home-container">
        <div className="brand-badge">AI Powered Code Assessment</div>

        <h1>
          Competitive Coding
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
              onClick={() => navigate("/student/join")}
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
    </div>
  );
}

export default HomePage;
