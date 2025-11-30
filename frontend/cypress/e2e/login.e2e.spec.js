// E2E Login scenarios (Cypress)

describe("Login E2E Scenarios", () => {
    const base = Cypress.env("FRONTEND_BASE") || "http://localhost:5173";
    const backend = Cypress.env("BACKEND_URL") || "http://localhost:8080";

    beforeEach(() => {
        cy.visit(base);
        cy.clearLocalStorage();
    });

    /**
     * Helper: Forward login requests to real backend
     */
    const forwardLoginToBackend = () => {
        cy.intercept(
            { method: "POST", url: "/api/auth/login" },
            { middleware: true },
            req => {
                const body = req.body;

                req.on("before:request", xhr => {
                    xhr.requestOptions.url = `${backend}/api/auth/login`;
                    xhr.requestOptions.headers = {
                        ...xhr.requestOptions.headers,
                        "content-type": "application/json",
                    };
                    xhr.requestOptions.body = JSON.stringify(body);
                });

                req.on("response", res => {
                    console.log("[Backend Response]", res?.statusCode, res?.body);
                });

                req.continue();
            }
        ).as("realLogin");
    };

    // ------------------------------------------------------------------------
    // a) Complete login flow
    // ------------------------------------------------------------------------
    it("a) Complete login flow (valid credentials → real API success)", () => {
        forwardLoginToBackend();

        const username = Cypress.env("E2E_USER") || "admin";
        const password = Cypress.env("E2E_PASS") || "abc123";

        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(password);
        cy.get('[data-testid="login-button"]').click();

        cy.wait("@realLogin")
            .its("response.statusCode")
            .should("eq", 200);

        cy.get('[data-testid="login-message"]').should("contain.text", "thanh cong");

    });

    // ------------------------------------------------------------------------
    // b) Validation messages
    // ------------------------------------------------------------------------
    it("b) Validation messages (empty fields & invalid format)", () => {
        cy.get('[data-testid="login-button"]').click();

        cy.get('[data-testid="username-error"]').should("be.visible");
        cy.get('[data-testid="password-error"]').should("be.visible");

        cy.get('[data-testid="username-input"]').type("ab");
        cy.get('[data-testid="password-input"]').type("12345");

        cy.get('[data-testid="login-button"]').click();

        cy.get('[data-testid="username-error"]').should(
            "contain.text",
            "Username must be between 3 and 50 characters"
        );

        cy.get('[data-testid="password-error"]').should(
            "contain.text",
            "Password must be between 6 and 100 characters"
        );
    });

    // ------------------------------------------------------------------------
    // c) Server error and retry success (real API)
    // ------------------------------------------------------------------------
    it("c) First login fails (401), second login succeeds", () => {
        let firstTime = true;

        cy.intercept("POST", "/api/auth/login", req => {
            if (firstTime) {
                firstTime = false;
                req.reply({
                    statusCode: 401,
                    body: { success: false, message: "Invalid username or password" }
                });
            } else {
                req.reply({
                    statusCode: 200,
                    body: { token: "abc123" }
                });
            }
        }).as("loginFlow");

        cy.visit(base);

        const username = Cypress.env("E2E_USER") || "admin";
        const goodPass = Cypress.env("E2E_PASS") || "abc123";
        const badPass = goodPass + "wrong";

        // First attempt → fail
        cy.get('[data-testid="username-input"]').type(username);
        cy.get('[data-testid="password-input"]').type(badPass);
        cy.get('[data-testid="login-button"]').click();

        cy.wait("@loginFlow")
            .its("response.statusCode")
            .should("eq", 401);

        cy.get('[data-testid="password-error"]').should("be.visible");

        // Second attempt → success
        cy.get('[data-testid="password-input"]').clear().type(goodPass);
        cy.get('[data-testid="username-input"]').clear().type(username);
        cy.get('[data-testid="login-button"]').click();

        cy.wait("@loginFlow")
            .its("response.statusCode")
            .should("eq", 200);

        cy.get('[data-testid="login-message"]').should("contain.text", "thanh cong");
    });

    // ------------------------------------------------------------------------
    // d) UI interactions (Enter key, focus)
    // ------------------------------------------------------------------------
    it("d) UI interactions: Enter key submits & focus behavior", () => {
        forwardLoginToBackend();

        const username = Cypress.env("E2E_USER") || "user";
        const password = Cypress.env("E2E_PASS") || "abc123";

        cy.get('[data-testid="username-input"]').focus().type(username);
        cy.get('[data-testid="password-input"]').type(password + "{enter}");

        cy.wait("@realLogin")
            .its("response.statusCode")
            .should("eq", 200);

        cy.get('[data-testid="login-message"]').should("contain.text", "thanh cong");
        cy.get('[data-testid="login-button"]').should("not.be.disabled");
    });
});
