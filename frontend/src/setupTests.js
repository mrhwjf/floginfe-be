// src/setupTests.js
import '@testing-library/jest-dom';
import axios from 'axios';
import AxiosMockAdapter from 'axios-mock-adapter';
import { TextEncoder, TextDecoder } from 'util';

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

// Use axios-mock-adapter in Jest environment to mock API calls instead of MSW.
const mock = new AxiosMockAdapter(axios);

const loginEndpointMatcher = /\/api\/auth\/login$/;

mock.onPost(loginEndpointMatcher).reply(async (config) => {
  // small artificial delay so UI has time to show loading state in tests
  await new Promise((r) => setTimeout(r, 20));
  try {
    const data = JSON.parse(config.data || '{}');
    const { username, password } = data;
    // Accept login when password satisfies rules (6-100 chars, at least one letter and one digit)
    const pwdValid = typeof password === 'string' && /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z0-9]{6,100}$/.test(password);
    if (pwdValid) {
      return [200, { token: 'fake-jwt-token' }];
    }
    return [401, { message: 'Invalid credentials' }];
  } catch (e) {
    return [400, { message: 'Bad request' }];
  }
});

// Provide a no-op alert implementation to avoid jsdom "Not implemented" errors
if (typeof globalThis.alert === 'undefined') {
  globalThis.alert = () => { };
}

afterEach(() => {
  mock.resetHistory();
});

export { };
