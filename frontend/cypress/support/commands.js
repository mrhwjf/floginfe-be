// custom Cypress commands can be added here
// kept minimal for now

// Example: cy.login(username, password)
Cypress.Commands.add('uiLogin', (username, password) => {
  cy.get('[data-testid="username-input"]').clear().type(username);
  cy.get('[data-testid="password-input"]').clear().type(password);
  cy.get('[data-testid="login-button"]').click();
});
