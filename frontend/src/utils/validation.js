/**
 * Validates username input
 * @param {string} username
 * @returns {string} error message or empty string
 */
export const validateUsername = (username) => {
  const trimmed = String(username || '').trim();

  if (!trimmed) {
    return 'Username is required';
  }

  if (trimmed.length < 3 || trimmed.length > 50) {
    return 'Username must be between 3 and 50 characters';
  }

  const USERNAME_REGEX = /^[A-Za-z0-9._-]+$/;
  if (!USERNAME_REGEX.test(trimmed)) {
    return "Username may only contain letters, numbers, '-', '.', and '_'";
  }

  return '';
};

/**
 * Validates password input
 * @param {string} password
 * @returns {string} error message or empty string
 */
export const validatePassword = (password) => {
  const trimmed = String(password || '').trim();

  if (!trimmed) {
    return 'Password is required';
  }

  if (trimmed.length < 6 || trimmed.length > 100) {
    return 'Password must be between 6 and 100 characters';
  }

  const hasLetter = /[A-Za-z]/.test(trimmed);
  const hasNumber = /[0-9]/.test(trimmed);
  if (!hasLetter || !hasNumber) {
    return 'Password must contain at least one letter and one number';
  }

  return '';
};

export const validateLoginForm = (username, password) => {
  return {
    username: validateUsername(username),
    password: validatePassword(password),
  };
};
