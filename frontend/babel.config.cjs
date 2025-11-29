module.exports = {
  presets: [
    // Use commonjs modules so Jest can use require internally and avoid ESM require errors
    ['@babel/preset-env', { targets: { node: 'current' }, modules: 'commonjs' }],
    ['@babel/preset-react', { runtime: 'automatic' }]
  ],
  plugins: []
};
