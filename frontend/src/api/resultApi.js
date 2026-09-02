import axiosInstance from "./axiosInstance";

export const getTestResults = async (testId) => {
  const response = await axiosInstance.get(`/admin/tests/${testId}/results`);

  return response.data;
};

export const getStudentResult = async (testId, attemptId) => {
  const response = await axiosInstance.get(
    `/admin/tests/${testId}/results/${attemptId}`,
  );

  return response.data;
};

export const overrideMarks = async (evaluationId, data) => {
  const response = await axiosInstance.patch(
    `/admin/evaluations/${evaluationId}/marks`,
    data,
  );

  return response.data;
};

export const reEvaluateTest = async (testId) => {
  const response = await axiosInstance.post(
    `/admin/tests/${testId}/reevaluate`,
  );

  return response.data;
};

export const reEvaluateStudent = async (testId, attemptId) => {
  const response = await axiosInstance.post(
    `/admin/tests/${testId}/results/${attemptId}/reevaluate`,
  );

  return response.data;
};