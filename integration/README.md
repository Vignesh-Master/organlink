# OrganLink Frontend-Backend Integration

This directory contains the integration code to connect the React frontend from `builder-quantum-studio` with the Spring Boot backend from `organlink`.

## Overview

The integration consists of:

1. **API Integration Service** - A JavaScript service that provides a unified API interface for the frontend to communicate with the backend.
2. **WebSocket Service** - A service that enables real-time communication between the frontend and backend.
3. **Proxy Configuration** - Configuration for the development server to proxy API requests to the Spring Boot backend.
4. **Setup Script** - A script to automate the integration process.

## Setup Instructions

### Automated Setup

Use the provided setup script to automate the integration process:

```bash
cd integration
setup-integration.bat
```

### Manual Setup

#### 1. Copy Integration Files

Copy the integration files to the appropriate locations:

```bash
# Copy API integration service to the frontend project
cp api-integration.js ../builder-quantum-studio/client/services/organlink-api.js

# Copy WebSocket service to the frontend project
cp websocket-service.js ../builder-quantum-studio/client/services/websocket-service.js

# Copy proxy configuration to the frontend project
cp proxy-config.js ../builder-quantum-studio/
```

#### 2. Update Frontend Configuration

Update the Vite configuration in `builder-quantum-studio/vite.config.ts` to use the proxy:

```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import proxyConfig from './proxy-config';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: proxyConfig
  }
});
```

#### 3. Import and Use the API Service

In your React components, import and use the API service and WebSocket service:

```javascript
import { authAPI, hospitalAPI, organizationAPI } from '../services/organlink-api';
import webSocketService from '../services/websocket-service';

// Example usage in a component
const LoginComponent = () => {
  const handleLogin = async (credentials) => {
    try {
      const response = await authAPI.hospitalLogin(credentials);
      // Handle successful login
      
      // Initialize WebSocket after successful login
      webSocketService.initialize();
      
      // Subscribe to notifications
      webSocketService.subscribe('/user/queue/notifications', (message) => {
        console.log('Received notification:', message);
      });
    } catch (error) {
      // Handle error
    }
  };
  
  // Component JSX
};
```

## Running the Integrated Application

1. **Start the Spring Boot Backend**:

```bash
cd organlink
mvn spring-boot:run
```

2. **Start the React Frontend**:

```bash
cd builder-quantum-studio
npm run dev
```

3. Access the application at `http://localhost:3000`

## API Endpoints

The integration service provides access to the following API endpoints:

- **Authentication API** - Login, logout, token validation
- **Location API** - Countries, states, cities, hospitals
- **Admin API** - System statistics, hospital and organization management
- **Hospital API** - Donor and patient management, matching
- **Organization API** - Policy management and voting
- **Blockchain API** - Transaction history and IPFS records

Refer to the `api-integration.js` file for detailed documentation of available methods.

## Troubleshooting

### CORS Issues

If you encounter CORS issues, ensure the Spring Boot backend has CORS configured properly in `WebSecurityConfig.java`:

```java
@Configuration
public class WebSecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Authentication Issues

Ensure the JWT token is being properly stored and included in API requests. Check the browser's local storage and network requests to verify.

## Security Considerations

- The integration uses JWT tokens for authentication
- Sensitive data should be transmitted over HTTPS in production
- API keys and secrets should be stored in environment variables, not hardcoded