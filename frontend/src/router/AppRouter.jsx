import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import HomePage from "../pages/HomePage";

import JoinTestPage from "../pages/student/JoinTestPage";
import TestPage from "../pages/student/TestPage";
import SubmissionSuccessPage from "../pages/student/SubmissionSuccessPage";

import TeacherDashboard from "../pages/teacher/TeacherDashboard";
import CreateTestPage from "../pages/teacher/CreateTestPage";
import TestResultsPage from "../pages/teacher/TestResultsPage";
import StudentResultPage from "../pages/teacher/StudentResultPage";
import TeacherLoginPage from "../pages/teacher/TeacherLoginPage";

import ProtectedTeacherRoute from "../components/auth/ProtectedTeacherRoute";
import SystemCheckPage from "../pages/student/SystemCheckPage";

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* =========================
            PUBLIC HOME ROUTE
        ========================== */}

        <Route path="/" element={<HomePage />} />

        {/* =========================
            PUBLIC STUDENT ROUTES
        ========================== */}
        <Route path="/student/system-check" element={<SystemCheckPage />} />

        <Route path="/student/join" element={<JoinTestPage />} />

        <Route path="/student/test/:attemptId" element={<TestPage />} />

        <Route path="/student/success" element={<SubmissionSuccessPage />} />

        {/* =========================
            PUBLIC TEACHER LOGIN
        ========================== */}

        <Route path="/teacher/login" element={<TeacherLoginPage />} />

        {/* =========================
            PROTECTED TEACHER ROUTES
        ========================== */}

        <Route
          path="/teacher"
          element={
            <ProtectedTeacherRoute>
              <TeacherDashboard />
            </ProtectedTeacherRoute>
          }
        />

        <Route
          path="/teacher/tests/create"
          element={
            <ProtectedTeacherRoute>
              <CreateTestPage />
            </ProtectedTeacherRoute>
          }
        />

        <Route
          path="/teacher/tests/:testId/results"
          element={
            <ProtectedTeacherRoute>
              <TestResultsPage />
            </ProtectedTeacherRoute>
          }
        />

        <Route
          path="/teacher/tests/:testId/results/:attemptId"
          element={
            <ProtectedTeacherRoute>
              <StudentResultPage />
            </ProtectedTeacherRoute>
          }
        />

        {/* =========================
            UNKNOWN ROUTES
        ========================== */}

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;
