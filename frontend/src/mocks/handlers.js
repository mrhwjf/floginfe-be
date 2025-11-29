// src/mocks/handlers.js
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.post('/api/auth/login', async ({ request }) => {
    const body = await request.json();
    const { username, password } = body || {};

    // password must contain at least one letter and one number and be 6-100 chars
    const pwdValid = typeof password === 'string' && /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z0-9]{6,100}$/.test(password);
    if (username === 'admin' && pwdValid) {
      return HttpResponse.json({ token: 'fake-jwt-token' }, { status: 200 });
    }
    return HttpResponse.json({ message: 'Invalid credentials' }, { status: 401 });
  }),
];
