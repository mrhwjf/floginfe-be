// E2E Login scenarios (Cypress)

describe('Login E2E Scenarios', () => {
    const base = 'http://localhost:5173';

    beforeEach(() => {
        // visit app root; ensure dev server running at this address
        cy.visit(base);
        // clear storage between tests
        cy.clearLocalStorage();
    });

    // a) Complete login flow
    it('Complete login flow: valid credentials -> token stored, success UI', () => {
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: { token: 'e2e-token-1' },
        }).as('loginRequest');

        cy.get('[data-testid="username-input"]').type('admin');
        cy.get('[data-testid="password-input"]').type('abc123');
        cy.get('[data-testid="login-button"]').click();

        // wait for request and assert success UI
        cy.wait('@loginRequest');
        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.window().then((win) => {
            expect(win.localStorage.getItem('token')).to.equal('e2e-token-1');
        });
    });

    // b) Validation messages
    it('Validation messages: empty and invalid inputs', () => {
        // submit with empty fields
        cy.get('[data-testid="login-button"]').click();
        cy.get('[data-testid="username-error"]').should('be.visible');
        cy.get('[data-testid="password-error"]').should('be.visible');

        // too-short username/password
        cy.get('[data-testid="username-input"]').type('ab');
        cy.get('[data-testid="password-input"]').type('12345');
        cy.get('[data-testid="login-button"]').click();
        cy.get('[data-testid="username-error"]').should('contain.text', 'Username must be between 3 and 50 characters');
        cy.get('[data-testid="password-error"]').should('contain.text', 'Password must be between 6 and 100 characters');
    });

    // c) Success/error flows
    it('Server error and retry success flows', () => {
        // first respond with 401, then with 200
        cy.intercept('POST', '/api/auth/login', (req) => {
            // use internal counter on window to return sequential responses
            if (!window.__e2e_call_count) window.__e2e_call_count = 0;
            window.__e2e_call_count += 1;
            if (window.__e2e_call_count === 1) {
                req.reply({ statusCode: 401, body: { message: 'Invalid credentials' } });
            } else {
                req.reply({ statusCode: 200, body: { token: 'retry-e2e-token' } });
            }
        }).as('loginSeq');

        cy.get('[data-testid="username-input"]').type('user');
        cy.get('[data-testid="password-input"]').type('abc123');
        cy.get('[data-testid="login-button"]').click();

        // after failure, password-error should show server message
        cy.wait('@loginSeq');
        cy.get('[data-testid="password-error"]').should('contain.text', 'Invalid credentials');

        // retry: click again to trigger queued 200 response
        cy.get('[data-testid="login-button"]').click();
        cy.wait('@loginSeq');
        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.window().then((win) => expect(win.localStorage.getItem('token')).to.equal('retry-e2e-token'));
    });

    // d) UI elements interactions
    it('UI interactions: Enter key submits and focus management', () => {
        // intercept to return success so we can assert post-submit effects
        cy.intercept('POST', '/api/auth/login', { statusCode: 200, body: { token: 'ui-token' } }).as('loginUI');

        cy.get('[data-testid="username-input"]').focus().should('have.focus');
        cy.get('[data-testid="username-input"]').type('user');
        cy.get('[data-testid="password-input"]').type('abc123');

        // press Enter on password input to submit
        cy.get('[data-testid="password-input"]').type('{enter}');
        cy.wait('@loginUI');
        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.get('[data-testid="login-button"]').should('not.be.disabled');
        cy.window().then((win) => expect(win.localStorage.getItem('token')).to.equal('ui-token'));
    });
});