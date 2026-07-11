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
