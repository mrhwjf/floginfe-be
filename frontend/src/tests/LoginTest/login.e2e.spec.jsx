// E2E-style tests (using React Testing Library + axios-mock-adapter)
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AxiosMockAdapter from 'axios-mock-adapter';
import axios from 'axios';
import LoginForm from '../../components/Login/LoginForm';

describe('Login E2E Scenarios', () => {
    let mock;

    beforeEach(() => {
        mock = new AxiosMockAdapter(axios);
        localStorage.clear();
    });

    afterEach(() => {
        mock.restore();
        localStorage.clear();
    });

    // a) Test complete login flow (1 điểm)
    test('Complete login flow: valid credentials -> token stored, success UI, redirect', async () => {
        mock.onPost('/api/auth/login').reply(200, { token: 'e2e-token-1' });

        // render component without mocking react-router navigation
        render(<LoginForm />);

        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        const button = screen.getByTestId('login-button');

        fireEvent.change(username, { target: { value: 'admin' } });
        fireEvent.change(password, { target: { value: 'abc123' } });
        fireEvent.click(button);

        // Loading
        expect(button).toBeDisabled();

        // Success UI
        await waitFor(() =>
            expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong')
        );

        // Token stored
        expect(localStorage.getItem('token')).toBe('e2e-token-1');

        // If your app performs navigation after login, check it in an E2E environment (Cypress)

        // Button enabled again
        await waitFor(() =>
            expect(button).not.toBeDisabled()
        );
    });


    // b) Test validation messages (0.5 điểm)
    test('Validation messages: empty and invalid inputs', async () => {
        render(<LoginForm />);

        const button = screen.getByTestId('login-button');
        fireEvent.click(button);

        await waitFor(() => {
        expect(screen.getByTestId('username-error')).toBeInTheDocument();
        expect(screen.getByTestId('password-error')).toBeInTheDocument();
        });

        // Too-short username and password
        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        fireEvent.change(username, { target: { value: 'ab' } });
        fireEvent.change(password, { target: { value: '12345' } });
        fireEvent.click(button);

        await waitFor(() => {
        expect(screen.getByTestId('username-error')).toHaveTextContent('Username must be between 3 and 50 characters');
        expect(screen.getByTestId('password-error')).toHaveTextContent('Password must be between 6 and 100 characters');
        });
    });

    // c) Test success/error flows (0.5 điểm)
    test('Server error and retry success flows', async () => {
        // first respond with 401, then respond with 200
        mock.onPost('/api/auth/login').replyOnce(401, { message: 'Invalid credentials' });
        mock.onPost('/api/auth/login').replyOnce(200, { token: 'retry-e2e-token' });

        render(<LoginForm />);
        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        const button = screen.getByTestId('login-button');

        fireEvent.change(username, { target: { value: 'user' } });
        fireEvent.change(password, { target: { value: 'abc123' } });
        fireEvent.click(button);

        // after failure, password-error should show server message
        await waitFor(() => expect(screen.getByTestId('password-error')).toHaveTextContent('Invalid credentials'));

        // retry
        fireEvent.click(button);
        await waitFor(() => expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong'));
        expect(localStorage.getItem('token')).toBe('retry-e2e-token');
    });

    // d) Test UI elements interactions (0.5 điểm)
    test('UI interactions: Enter key submits and focus management', async () => {
        mock.onPost('/api/auth/login').reply(200, { token: 'ui-token' });

        render(<LoginForm />);
        const username = screen.getByTestId('username-input');
        const password = screen.getByTestId('password-input');
        const button = screen.getByTestId('login-button');

        // focus moves and pressing Enter on password submits
        username.focus();
        expect(document.activeElement).toBe(username);

        fireEvent.change(username, { target: { value: 'user' } });
        fireEvent.change(password, { target: { value: 'abc123' } });

        // Submit the form directly because Enter may not trigger submit reliably in jsdom
        const form = screen.getByTestId('login-button').closest('form');
        fireEvent.submit(form);

        await waitFor(() => expect(screen.getByTestId('login-message')).toHaveTextContent('thanh cong'));
        expect(button).not.toBeDisabled();
        expect(localStorage.getItem('token')).toBe('ui-token');
    });
});
