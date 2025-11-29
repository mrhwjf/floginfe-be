import { validateUsername, validatePassword, validateLoginForm } from '../../utils/validation';

describe('Login Validation Tests', () => {
  
  // ========== VALIDATION UNIT TESTS ==========

  describe('Unit Tests - Validation Functions', () => {
    // Mapping unit tests to TC_LOGIN_01..TC_LOGIN_11 focusing on validation outcomes

    test('TC_LOGIN_01 - Valid Login (validation) -> no errors', () => {
      const res = validateLoginForm('admin', 'abc123');
      expect(res.username).toBe('');
      expect(res.password).toBe('');
    });

    test('TC_LOGIN_02 - Empty Username -> username required', () => {
      const res = validateLoginForm('', 'abc123');
      expect(res.username).toBe('Username is required');
      expect(res.password).toBe('');
    });

    test('TC_LOGIN_03 - Empty Password -> password required', () => {
      const res = validateLoginForm('admin', '');
      expect(res.username).toBe('');
      expect(res.password).toBe('Password is required');
    });

    test('TC_LOGIN_04 - Username too short -> length error', () => {
      expect(validateUsername('ab')).toBe('Username must be between 3 and 50 characters');
    });

    test('TC_LOGIN_05 - Password too short -> length error', () => {
      expect(validatePassword('12345')).toBe('Password must be between 6 and 100 characters');
    });

    test('TC_LOGIN_06 - Invalid credentials case (password too short) -> length error', () => {
      // file used password 'wrong' which is only 5 chars -> expect length error
      expect(validatePassword('wrong')).toBe('Password must be between 6 and 100 characters');
    });

    test('TC_LOGIN_07 - Both fields empty -> both required errors', () => {
      const res = validateLoginForm('', '');
      expect(res.username).toBe('Username is required');
      expect(res.password).toBe('Password is required');
    });

    test('TC_LOGIN_08 - Username with special characters -> username char error', () => {
      expect(validateUsername('admin@123')).toBe("Username may only contain letters, numbers, '-', '.', and '_'");
    });

    test('TC_LOGIN_09 - Leading/trailing spaces -> trimmed and valid', () => {
      const res = validateLoginForm('  admin  ', ' abc123 ');
      expect(res.username).toBe('');
      expect(res.password).toBe('');
    });

    test('TC_LOGIN_10 - Username too long (>50) -> length error', () => {
      const long = 'a'.repeat(51);
      expect(validateUsername(long)).toBe('Username must be between 3 and 50 characters');
    });

    test('TC_LOGIN_11 - Invalid password format (letters-only or digits-only)', () => {
      expect(validatePassword('abcdef')).toBe('Password must contain at least one letter and one number');
      expect(validatePassword('123456')).toBe('Password must contain at least one letter and one number');
    });
  });
});