export default {
  testEnvironment: 'jsdom',
  extensionsToTreatAsEsm: ['.jsx'],
  // type: module in package.json already marks .js as ESM; keep config minimal
  transform: {
        // Áp dụng Babel cho các file .js, .jsx, .mjs (đảm bảo nó chuyển đổi JSX)
        '^.+\\.(js|jsx|mjs)$': 'babel-jest',
    },
  moduleNameMapper: {
    // Bỏ qua import CSS trong Jest
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
  },
};
