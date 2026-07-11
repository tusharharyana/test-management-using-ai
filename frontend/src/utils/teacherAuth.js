const TEACHER_ID = "e19866";
const TEACHER_PASSWORD = "nahi";

const AUTH_KEY = "teacher_authenticated";

export const loginTeacher = (teacherId, password) => {
  if (teacherId === TEACHER_ID && password === TEACHER_PASSWORD) {
    sessionStorage.setItem(AUTH_KEY, "true");
    return true;
  }

  return false;
};

export const isTeacherAuthenticated = () => {
  return sessionStorage.getItem(AUTH_KEY) === "true";
};

export const logoutTeacher = () => {
  sessionStorage.removeItem(AUTH_KEY);
};
