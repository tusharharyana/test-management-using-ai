import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import HomePage from "../pages/HomePage";
import JoinTestPage from "../pages/student/JoinTestPage";
import TestPage from "../pages/student/TestPage";
import SubmissionSuccessPage from "../pages/student/SubmissionSuccessPage";

import TeacherDashboard from "../pages/teacher/TeacherDashboard";
import CreateTestPage from "../pages/teacher/CreateTestPage";
import TestResultsPage from "../pages/teacher/TestResultsPage";
import StudentResultPage from "../pages/teacher/StudentResultPage";

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />

        {/* Student routes */}

        <Route path="/student/join" element={<JoinTestPage />} />

        <Route path="/student/test/:attemptId" element={<TestPage />} />

        <Route path="/student/success" element={<SubmissionSuccessPage />} />

        {/* Teacher routes */}

        <Route path="/teacher" element={<TeacherDashboard />} />

        <Route path="/teacher/tests/create" element={<CreateTestPage />} />

        <Route
          path="/teacher/tests/:testId/results"
          element={<TestResultsPage />}
        />

        <Route
          path="/teacher/tests/:testId/results/:attemptId"
          element={<StudentResultPage />}
        />

        {/* Unknown routes */}

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;
