import { Navigate, useLocation } from "react-router-dom";
import { isTeacherAuthenticated } from "../../utils/teacherAuth";

function ProtectedTeacherRoute({ children }) {
  const location = useLocation();

  if (!isTeacherAuthenticated()) {
    return (
      <Navigate
        to="/teacher/login"
        state={{ from: location.pathname }}
        replace
      />
    );
  }

  return children;
}

export default ProtectedTeacherRoute;
