const LoginPage = require('../support/pages/LoginPage');

describe('Login E2E - Cypress', () => {
  it('successful login stores token and shows success', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 200, body: { token: 'cypress-token' } }).as('loginReq');
    LoginPage.visit();
    LoginPage.fillUsername('admin');
    LoginPage.fillPassword('abc123');
    LoginPage.submit();
    cy.wait('@loginReq');
    LoginPage.getLoginMessage().should('contain.text', 'thanh cong');
    cy.window().then((win) => {
      expect(win.localStorage.getItem('token')).to.equal('cypress-token');
    });
  });

  it('incorrect password shows server error', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: { message: 'Incorrect password' } }).as('loginReq');
    LoginPage.visit();
    LoginPage.fillUsername('testuser');
    LoginPage.fillPassword('Wrong123');
    LoginPage.submit();
    cy.wait('@loginReq');
    LoginPage.getPasswordError().should('contain.text', 'Incorrect password');
  });

  it('username not found shows server error', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 404, body: { message: 'User not found' } }).as('loginReq');
    LoginPage.visit();
    LoginPage.fillUsername('no-such-user');
    LoginPage.fillPassword('SomePass123');
    LoginPage.submit();
    cy.wait('@loginReq');
    LoginPage.getPasswordError().should('contain.text', 'User not found');
  });
});
