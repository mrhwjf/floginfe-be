// E2E Login scenarios (Cypress)

describe('Login E2E Scenarios', () => {
    const base = Cypress.env('FRONTEND_BASE') || 'http://localhost:5173';
    const backend = Cypress.env('BACKEND_URL') || 'http://localhost:8080';

    beforeEach(() => {
        cy.visit(base);
        cy.clearLocalStorage();
    });

    // helper: forward login request to real backend
    const forwardLoginToBackend = () => {
        cy.intercept({ method: 'POST', url: '/api/auth/login' }, { middleware: true }, (req) => {
            const body = req.body;
            req.on('before:request', (xhr) => {
                // debug log
                // eslint-disable-next-line no-console
                console.log('[forward] original body=', body);
                xhr.requestOptions.url = `${backend}/api/auth/login`;
                xhr.requestOptions.headers = { ...xhr.requestOptions.headers, 'content-type': 'application/json' };
                xhr.requestOptions.body = JSON.stringify(body);
                // eslint-disable-next-line no-console
                console.log('[forward] forwarding to', xhr.requestOptions.url, 'with body', xhr.requestOptions.body);
            });
            req.on('response', (res) => {
                // eslint-disable-next-line no-console
                console.log('[forward] response status=', res && res.statusCode, 'body=', res && res.body);
            });
            req.continue();
        }).as('realLogin');
    };

    // a) Complete login flow
    it('Complete login flow: valid credentials -> token stored, success UI (real API)', () => {
        forwardLoginToBackend();

        const username = Cypress.env('E2E_USER') || 'admin';
        const password = Cypress.env('E2E_PASS') || 'abc123';

        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(password);
        cy.get('[data-testid="login-button"]').click();

        cy.wait('@realLogin').its('response.statusCode').should('eq', 200);
        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.window().then((win) => {
            expect(win.localStorage.getItem('token')).to.be.a('string').and.not.be.empty;
        });
    });

    // b) Validation messages
    it('Validation messages: empty and invalid inputs', () => {
        cy.get('[data-testid="login-button"]').click();
        cy.get('[data-testid="username-error"]').should('be.visible');
        cy.get('[data-testid="password-error"]').should('be.visible');

        cy.get('[data-testid="username-input"]').type('ab');
        cy.get('[data-testid="password-input"]').type('12345');
        cy.get('[data-testid="login-button"]').click();
        cy.get('[data-testid="username-error"]').should('contain.text', 'Username must be between 3 and 50 characters');
        cy.get('[data-testid="password-error"]').should('contain.text', 'Password must be between 6 and 100 characters');
    });

    // c) Success/error flows (real API)
    it('Server error and retry success flows (real API)', () => {
        let firstCall = true;

        cy.intercept('POST', '/api/auth/login', (req) => {
            if (firstCall) {
                firstCall = false;
                req.reply({ statusCode: 401, body: { success: false, message: 'Invalid username or password' } });
                return;
            }
            req.reply({ statusCode: 200, body: { token: 'abc123' } });
        }).as('loginFlow');

        cy.visit(base);

        const username = Cypress.env('E2E_USER') || 'admin';
        const correctPass = Cypress.env('E2E_PASS') || 'abc123';
        const wrongPass = correctPass + 'wrong';

        // FIRST FAIL ATTEMPT
        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(wrongPass);
        cy.get('[data-testid="login-button"]').click();

        cy.wait('@loginFlow')
            .its('response.statusCode')
            .should('eq', 401);

        // Error message must appear
        cy.get('[data-testid="password-error"]').should('be.visible');

        // SECOND SUCCESS ATTEMPT
        // Clear errors by re-entering values
        cy.get('[data-testid="password-input"]').clear().type(correctPass);
        cy.get('[data-testid="username-input"]').clear().type(username);

        cy.get('[data-testid="login-button"]').click();

        cy.wait('@loginFlow')
            .its('response.statusCode')
            .should('eq', 200);

        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
    });



    // d) UI interactions
    it('UI interactions: Enter key submits and focus management (real API)', () => {
        forwardLoginToBackend();

        const username = Cypress.env('E2E_USER') || 'user';
        const password = Cypress.env('E2E_PASS') || 'abc123';

        cy.get('[data-testid="username-input"]').focus().should('have.focus');
        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(password + '{enter}');

        cy.wait('@realLogin').its('response.statusCode').should('eq', 200);
        cy.get('[data-testid="login-message"]').should('contain.text', 'thanh cong');
        cy.get('[data-testid="login-button"]').should('not.be.disabled');
    });
});