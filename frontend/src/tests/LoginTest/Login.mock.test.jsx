// ============ Mock external dependencies cho Login component:================

import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Login from '../../components/Login/LoginForm';
import * as authService from '../../services/authService';

jest.mock('../../services/authService');

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => jest.fn(),
}));


describe('Login Mock Tests', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });
    test('Mock: Login thanh cong', async () => {
        authService.loginUser.mockResolvedValue({
            success: true,
            token: 'mock-token-123',
            user: { username: 'testuser' }
        });
        render(<Login />);
        fireEvent.change(screen.getByTestId('username-input'), {
            target: { value: 'testuser' }
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'Test123' }
        });
        fireEvent.click(screen.getByTestId('login-button'));
        await waitFor(() => {
            expect(authService.loginUser).toHaveBeenCalledWith('testuser', 'Test123');
            expect(screen.getByText(/thanh cong/i)).toBeInTheDocument();
        });
    });
    test('Mock: Login that bai', async () => {
        authService.loginUser.mockRejectedValue({
            message: 'Invalid credentials'
        });
        render(<Login />);
        fireEvent.change(screen.getByTestId('username-input'), {
            target: { value: 'testuser' }
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'Test123' }
        });
        fireEvent.click(screen.getByTestId('login-button'));

        await waitFor(() => {
            // service should have been called with positional args as in this test suite
            expect(authService.loginUser).toHaveBeenCalledWith('testuser', 'Test123');
            // component shows server error in password-error element
            expect(screen.getByTestId('password-error')).toHaveTextContent('Invalid credentials');
        });
    });

    // Test với mocked successful/failed responses
    test('Mock: successful login shows success message', async () => {
        authService.loginUser.mockResolvedValue({ token: 'mock-token-123' });

        render(<Login />);
        fireEvent.change(screen.getByTestId('username-input'), { target: { value: 'testuser' } });
        fireEvent.change(screen.getByTestId('password-input'), { target: { value: 'Test123' } });
        fireEvent.click(screen.getByTestId('login-button'));

        await waitFor(() => {
            expect(authService.loginUser).toHaveBeenCalledWith('testuser', 'Test123');
            expect(screen.getByText(/thanh cong/i)).toBeInTheDocument();
        });
    });

    test('Mock: loading state while login is in flight', async () => {
        // Simulate a delayed resolved promise
        authService.loginUser.mockImplementationOnce(() => new Promise((res) => setTimeout(() => res({ token: 'd-token' }), 60)));

        render(<Login />);
        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        const button = screen.getByTestId('login-button');

        fireEvent.change(username, { target: { value: 'testuser' } });
        fireEvent.change(password, { target: { value: 'Test123' } });
        fireEvent.click(button);

        // Button should be disabled while request pending
        expect(button).toBeDisabled();

        await waitFor(() => expect(screen.getByTestId('login-message')).toBeInTheDocument());
    });

    test('Mock: does not call service when validation fails', async () => {
        // Leave fields empty
        render(<Login />);
        fireEvent.click(screen.getByTestId('login-button'));

        await waitFor(() => {
            expect(authService.loginUser).not.toHaveBeenCalled();
            expect(screen.getByTestId('username-error')).toBeInTheDocument();
        });
    });

    test('Mock: failed then successful retry shows success message', async () => {
        authService.loginUser
            .mockRejectedValueOnce({ message: 'Invalid credentials' })
            .mockResolvedValueOnce({ token: 'retry-token' });

        render(<Login />);
        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        const button = screen.getByTestId('login-button');

        fireEvent.change(username, { target: { value: 'testuser' } });
        fireEvent.change(password, { target: { value: 'Test123' } });
        fireEvent.click(button);

        // after first failure
        await waitFor(() => {
            expect(screen.getByTestId('password-error')).toHaveTextContent('Invalid credentials');
        });

        // retry
        fireEvent.click(button);
        await waitFor(() => {
            expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong');
        });

        // ensure service was called twice (first failure, then success)
        expect(authService.loginUser).toHaveBeenCalledTimes(2);
    });

    // Verify mock calls
    test('Verify mock functions are mocked', () => {
        // Ensure exported functions on authService are Jest mock functions
        Object.keys(authService).forEach((key) => {
            const val = authService[key];
            if (typeof val === 'function') {
                expect(jest.isMockFunction(val)).toBe(true);
            }
        });
    });
});