
import LoginPage from '../pages/LoginPage';
import { LOGIN_API } from '../support/e2e';

describe('Login E2E Scenarios', () => {

    beforeEach(() => {
        ;
        LoginPage.visit();
    });


    it('Display login page', () => {
        cy.url().should('include', '/auth/login');
        cy.get('[data-testid="login-form"]').should('be.visible');
    });

    // a) Complete login flow
    it('Complete login flow: valid credentials + success UI', () => {
        const username = Cypress.env('E2E_USER') || 'admin';
        const password = Cypress.env('E2E_PASS') || 'abc123';

        LoginPage.fillUsername(username);
        LoginPage.fillPassword(password);
        LoginPage.submit();

        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.wait(10000); // Wait for redirection
        cy.url().should('include', '/dashboard');
    });

    // b) Validation messages
    it('Validation messages: empty and invalid inputs', () => {
        LoginPage.submit();
        LoginPage.getUsernameError().should('be.visible');
        LoginPage.getPasswordError().should('be.visible');

        LoginPage.fillUsername('ab');
        LoginPage.fillPassword('12345');
        LoginPage.submit();
        LoginPage.getUsernameError().should('contain.text', 'Username must be between 3 and 50 characters');
        LoginPage.getPasswordError().should('contain.text', 'Password must be between 6 and 100 characters');
    });

    // c) Success/error flows
    it('Server error and retry success flows (real API)', () => {
        let firstCall = true;

        const username = Cypress.env('E2E_USER') || 'admin';
        const correctPass = Cypress.env('E2E_PASS') || 'abc123';
        const wrongPass = correctPass + 'wrong';

        // FIRST FAIL ATTEMPT
        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(wrongPass);
        cy.get('[data-testid="login-button"]').click();

        // Error message must appear
        cy.get('[data-testid="password-error"]').should('be.visible');

        // SECOND SUCCESS ATTEMPT
        // Clear errors by re-entering values
        LoginPage.fillPassword(correctPass);
        LoginPage.fillUsername(username);
        LoginPage.submit();

        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
    });



    // d) UI interactions
    it('UI interactions: Tab key navigates and Enter submits', () => {

        const username = Cypress.env('E2E_USER') || 'user';
        const password = Cypress.env('E2E_PASS') || 'abc123';

        cy.get('[data-testid="username-input"]').focus();
        cy.focused().should('have.attr', 'data-testid', 'username-input');
        cy.focused().type(username);
        cy.get('[data-testid="password-input"]').focus();
        cy.focused().should('have.attr', 'data-testid', 'password-input');

        cy.focused().type(password + '{enter}');

        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.get('[data-testid="login-button"]').should('not.be.disabled');
    });
});
