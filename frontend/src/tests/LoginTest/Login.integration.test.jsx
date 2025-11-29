// src/tests/LoginForm.test.tsx
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import LoginForm from '../../components/Login/LoginForm';
import axios from 'axios';
import AxiosMockAdapter from 'axios-mock-adapter';
describe('Login Validation Tests', () => {
    beforeEach(() => {
        localStorage.clear();
    });
    afterEach(() => {
        localStorage.clear();
    });
  
  // ========== INTEGRATION TESTS  ==========

    describe("Login Component Integration Tests", () => {

        // Test rendering và user interactions
        test("Hien thi loi khi submit form rong", async () => {
            render(<LoginForm />);
            const submitButton = screen.getByTestId("login-button");
            fireEvent.click(submitButton);
            await waitFor(() => {
                expect(screen.getByTestId("username-error"))
                .toBeInTheDocument();
            });
        });
        test("Goi API khi submit form hop le", async () => {
            render(<LoginForm />);
            const usernameInput = screen.getByTestId("username-input");
            const passwordInput = screen.getByTestId("password-input");
            const submitButton = screen.getByTestId("login-button");
            fireEvent.change(usernameInput, {
                target: { value: "testuser" }
            });
            fireEvent.change(passwordInput, {
                target: { value: "Test123" }
            });
            fireEvent.click(submitButton);
            await waitFor(() => {
                expect(screen.getByTestId("login-message"))
                .toHaveTextContent("thanh cong");
            });
        });

        // Test form submission và API calls
        test('Server rejects valid credentials -> shows server error', async () => {
            const mock = new AxiosMockAdapter(axios);
            try {
                mock.onPost('/api/auth/login').replyOnce(401, { message: 'Invalid credentials' });

                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'testuser' } });
                fireEvent.change(passwordInput, { target: { value: 'Test123' } });
                fireEvent.click(submitButton);

                await waitFor(() => {
                    expect(screen.getByTestId('password-error')).toHaveTextContent('Invalid credentials');
                });
            } finally {
                mock.restore();
            }
        });

        test('Shows loading state and stores token on success', async () => {
            const mock = new AxiosMockAdapter(axios);
            try {
                // Delay the response so we can assert loading state
                mock.onPost('/api/auth/login').replyOnce(() => new Promise((resolve) => setTimeout(() => resolve([200, { token: 'abc-token' }]), 50)));

                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'testuser' } });
                fireEvent.change(passwordInput, { target: { value: 'Test123' } });
                fireEvent.click(submitButton);

                // Button should be disabled while request is in flight
                expect(submitButton).toBeDisabled();

                await waitFor(() => expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong'));

                // Token should be stored
                expect(localStorage.getItem('token')).toBe('abc-token');
            } finally {
                mock.restore();
            }
        });

        // Test error handling và success messages
        test('Shows validation errors for empty fields', async () => {
            render(<LoginForm />);
            const submitButton = screen.getByTestId('login-button');
            fireEvent.click(submitButton);

            await waitFor(() => {
                expect(screen.getByTestId('username-error')).toBeInTheDocument();
                expect(screen.getByTestId('password-error')).toBeInTheDocument();
            });
        });

        test('Clears server error after successful retry', async () => {
            const mock = new AxiosMockAdapter(axios);
            try {
                // First response: reject, then accept on retry
                mock.onPost('/api/auth/login').replyOnce(401, { message: 'Invalid credentials' });
                mock.onPost('/api/auth/login').replyOnce(200, { token: 'retry-token' });

                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'testuser' } });
                fireEvent.change(passwordInput, { target: { value: 'Test123' } });
                fireEvent.click(submitButton);

                // Expect server error after first attempt
                await waitFor(() => {
                    expect(screen.getByTestId('password-error')).toHaveTextContent('Invalid credentials');
                });

                // Retry: click again to trigger queued 200 response
                fireEvent.click(submitButton);

                await waitFor(() => {
                    expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong');
                });

                // Ensure server error no longer shown and token stored
                expect(screen.queryByTestId('password-error')).toBeNull();
                expect(localStorage.getItem('token')).toBe('retry-token');
            } finally {
                mock.restore();
            }
        });

        test('Success message has correct styling', async () => {
            const mock = new AxiosMockAdapter(axios);
            try {
                mock.onPost('/api/auth/login').replyOnce(200, { token: 'style-token' });
                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'user' } });
                fireEvent.change(passwordInput, { target: { value: 'abc123' } });
                fireEvent.click(submitButton);

                await waitFor(() => expect(screen.getByTestId('login-message')).toBeInTheDocument());
                const msg = screen.getByTestId('login-message');
                // With Tailwind we expect a utility class for green text
                expect(msg).toHaveClass('text-green-600');
            } finally {
                mock.restore();
            }
        });
    });

});
