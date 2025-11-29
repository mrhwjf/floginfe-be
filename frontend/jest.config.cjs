module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.(js|jsx|ts|tsx)$': 'babel-jest',
  },
  moduleFileExtensions: ['js','jsx','ts','tsx','json'],
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
  // ignore Cypress specs and E2E-style files in src/tests
  testPathIgnorePatterns: [
    '<rootDir>/cypress/',
    '<rootDir>/src/tests/.*\\.e2e\\.spec\\.(js|jsx|ts|tsx)$'
  ],
};