import axiosInstance from "./axiosInstance";

export const runCode = async (codeData) => {
  const response = await axiosInstance.post("/code/run", codeData);

  return response.data;
};
