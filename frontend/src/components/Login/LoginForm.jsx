// src/components/Login/LoginForm.jsx
import React, { useState } from "react";
import { validateUsername, validatePassword } from "../../utils/validation";

import { loginUser, storeToken } from "../../services/authService";
import { useNavigate } from "react-router-dom";

const LoginForm = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState(null);

  const navigate = useNavigate();

  const validate = () => {
    const usernameError = validateUsername(username);
    const passwordError = validatePassword(password);

    const newErrors = {};
    if (usernameError) newErrors.username = usernameError;
    if (passwordError) newErrors.password = passwordError;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setSuccessMessage(null);

    try {
      const response = await loginUser(username.trim(), password.trim());
      setSuccessMessage('thanh cong');
      setTimeout(() => {
        navigate('/dashboard');
      }, 5000);
    } catch (err) {
      let message = "Login failed";
      if (err && err.message) {
        message = err.message;
      }
      setErrors({ password: message });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 p-4">
      <form onSubmit={handleSubmit} className="w-full max-w-md bg-white dark:bg-gray-800 rounded-lg shadow-md p-6"
        data-testid="login-form">
        {successMessage && (
          <p data-testid="login-message" className="text-green-600 mb-4">{successMessage}</p>
        )}

        <h2 className="text-2xl font-semibold mb-4 text-gray-800 dark:text-gray-100">Sign in to your account</h2>

        <div className="mb-4">
          <label htmlFor="username" className="block text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">Username</label>
          <input
            id="username"
            data-testid="username-input"
            value={username}
            onChange={(e) => { setUsername(e.target.value); setErrors({}); }}
            aria-label="username"
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-gray-100"
          />
          {errors.username && (
            <p data-testid="username-error" className="text-sm text-red-600 mt-1">{errors.username}</p>
          )}
        </div>

        <div className="mb-4">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 dark:text-gray-200 mb-1">Password</label>
          <input
            id="password"
            data-testid="password-input"
            type="password"
            value={password}
            onChange={(e) => { setPassword(e.target.value); setErrors({}); }}
            aria-label="password"
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-gray-100"
          />
          {errors.password && (
            <p data-testid="password-error" className="text-sm text-red-600 mt-1">{errors.password}</p>
          )}
        </div>

        <div className="flex items-center justify-between">
          <button
            data-testid="login-button"
            type="submit"
            disabled={isLoading}
            className="inline-flex items-center px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-md disabled:opacity-60"
          >
            {isLoading ? "Logging in..." : "Login"}
          </button>
        </div>
      </form>
    </div>

  );
};

export default LoginForm;
