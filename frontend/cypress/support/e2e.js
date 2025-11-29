// cypress/support/e2e.js
require('./commands');

// ignore uncaught exceptions from the app during tests
Cypress.on('uncaught:exception', (err, runnable) => {
  return false;
});
