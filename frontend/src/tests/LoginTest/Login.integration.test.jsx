// src/tests/LoginForm.test.tsx
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import LoginForm from '../../components/Login/LoginForm';
import * as authService from '../../services/authService';
jest.mock('../../services/authService');

const LOGIN_API_URL = '/api/auth/login';

jest.mock('react-router-dom', () => ({
    useNavigate: () => jest.fn(),
    // provide a minimal Link component for rendering in tests
    Link: ({ children, ...props }) => children || null,
}), { virtual: true });


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
            authService.loginUser.mockRejectedValueOnce(new Error('Invalid credentials'));

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
        });

        test('Shows loading state and stores token on success', async () => {
            // Delay the response so we can assert loading state
            authService.loginUser.mockImplementationOnce(() => new Promise((resolve) => setTimeout(() => resolve({ token: 'abc-token' }), 50)));

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

                // After success the submit button becomes enabled again
                expect(submitButton).not.toBeDisabled();
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
                // First response: reject, then accept on retry
                authService.loginUser
                    .mockRejectedValueOnce(new Error('Invalid credentials'))
                    .mockResolvedValueOnce({ token: 'retry-token' });

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

                // Ensure server error no longer shown and form is usable again
                expect(screen.queryByTestId('password-error')).toBeNull();
                expect(submitButton).not.toBeDisabled();
            });

        test('Success message has correct styling', async () => {
                authService.loginUser.mockResolvedValueOnce({ token: 'style-token' });
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
            });

        test('Server: incorrect password (401) -> shows password error', async () => {
                authService.loginUser.mockRejectedValueOnce(new Error('Incorrect password'));

                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'testuser' } });
                // use a password that passes client-side validation (letters+numbers)
                fireEvent.change(passwordInput, { target: { value: 'Wrong123' } });
                fireEvent.click(submitButton);

                await waitFor(() => {
                    expect(screen.getByTestId('password-error')).toHaveTextContent('Incorrect password');
                });
        });

        test('Server: username not found (404) -> shows server message in password-error', async () => {
                authService.loginUser.mockRejectedValueOnce(new Error('User not found'));

                render(<LoginForm />);
                const usernameInput = screen.getByTestId('username-input');
                const passwordInput = screen.getByTestId('password-input');
                const submitButton = screen.getByTestId('login-button');

                fireEvent.change(usernameInput, { target: { value: 'no-such-user' } });
                fireEvent.change(passwordInput, { target: { value: 'SomePass123' } });
                fireEvent.click(submitButton);

                await waitFor(() => {
                    // Current component maps server error into password-error
                    expect(screen.getByTestId('password-error')).toHaveTextContent('User not found');
                });
        });

    });

});
