import axios from 'axios';

const API_AUTH_URL = 'http://localhost:8080/api/auth'; // Giả định API endpoint

/**
 * Authenticates user with username and password
 * @param {{username: string, password: string}} credentials
 * @returns Promise resolving to response data (expects { token })
 */
/**
 * Accepts either (username, password) or a single credentials object { username, password }
 * and returns the response data (expects { token }).
 */
export const loginUser = async (arg1, arg2) => {
  const credentials =
    typeof arg1 === 'string' && typeof arg2 === 'string'
      ? { username: arg1, password: arg2 }
      : (arg1 || {});

  try {
    const response = await axios.post(`${API_AUTH_URL}/login`, credentials);
    return response.data;
  } catch (error) {
    if (error && error.response && error.response.data && error.response.data.message) {
      throw new Error(String(error.response.data.message));
    }
    throw error;
  }
};

export const storeToken = (token) => {
  localStorage.setItem('token', token);
};

export const getToken = () => {
  return localStorage.getItem('token');
};

export const clearToken = () => {
  localStorage.removeItem('token');
};
