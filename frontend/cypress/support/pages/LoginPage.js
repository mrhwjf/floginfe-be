class LoginPage {
  visit() {
    cy.visit('/');
  }

  fillUsername(username) {
    cy.get('[data-testid="username-input"]').clear().type(username);
  }

  fillPassword(password) {
    cy.get('[data-testid="password-input"]').clear().type(password);
  }

  submit() {
    cy.get('[data-testid="login-button"]').click();
  }

  getPasswordError() {
    return cy.get('[data-testid="password-error"]');
  }

  getUsernameError() {
    return cy.get('[data-testid="username-error"]');
  }

  getLoginMessage() {
    return cy.get('[data-testid="login-message"]');
  }
}

module.exports = new LoginPage();
