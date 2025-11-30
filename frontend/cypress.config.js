import { defineConfig } from "cypress";

export default defineConfig({
	e2e: {
		baseUrl: "http://localhost:5173",
		supportFile: "cypress/support/e2e.js",
		specPattern: ['cypress/e2e/**/*.cy.{js,jsx,ts,tsx}', 'cypress/e2e/**/*.spec.{js,jsx,ts,tsx}'],
		video: false,
		setupNodeEvents(on, config) {
			return config;
		},
		env: {
			backendUrl: "http://localhost:8080",
			VITE_USE_LS_MOCK: false,
			E2E_USER: "admin",
			E2E_PASS: "abc123"
		}
	}
});
