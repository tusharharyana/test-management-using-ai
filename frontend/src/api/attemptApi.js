import axiosInstance from "./axiosInstance";

export const startTest = async (studentData) => {
  const response = await axiosInstance.post("/attempts/start", studentData);

  return response.data;
};

export const submitTestAttempt = async (attemptId) => {
  const response = await axiosInstance.post(`/attempts/${attemptId}/submit`);

  return response.data;
};

export const deleteAttempt = async (attemptId) => {
  const response = await axiosInstance.delete(`/attempts/${attemptId}`);

  return response.data;
};
