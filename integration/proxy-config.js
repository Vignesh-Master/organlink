/**
 * OrganLink Proxy Configuration
 * 
 * This file configures the proxy settings for the React frontend
 * to communicate with the Spring Boot backend.
 */

module.exports = {
  // Proxy API requests to the Spring Boot backend
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    secure: false,
    logLevel: 'debug',
    pathRewrite: {
      '^/api': '/api/v1' // Rewrite path to match Spring Boot context path
    },
    onProxyRes: function(proxyRes, req, res) {
      // Log proxy responses
      console.log(`Proxied response from ${req.method} ${req.url} with status ${proxyRes.statusCode}`);
    }
  },
  
  // Proxy WebSocket connections for real-time features
  '/ws': {
    target: 'ws://localhost:8081',
    ws: true,
    changeOrigin: true
  }
};