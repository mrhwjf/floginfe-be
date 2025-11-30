module.exports = {
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx,html}'
  ],
  theme: {
    extend: {},
  },
  safelist: [
    { pattern: /^(bg|text|hover:bg|dark:bg)-(gray|indigo|green|red)-(50|100|200|300|400|500|600|700|800|900)$/ },
    { pattern: /^text-(sm|base|-lg|2xl|3xl|4xl|5xl|6xl)$/ },
  ],
  plugins: [],
}
