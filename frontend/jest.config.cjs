module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.(js|jsx|ts|tsx)$': 'babel-jest',
  },
  moduleFileExtensions: ['js','jsx','ts','tsx','json'],
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.js'],
};