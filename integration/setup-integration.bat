@echo off
echo ===================================================
echo OrganLink Frontend-Backend Integration Setup
echo ===================================================
echo.

REM Check if builder-quantum-studio exists
if not exist "..\..\builder-quantum-studio" (
    echo Error: builder-quantum-studio directory not found.
    echo Please ensure both projects are in the same parent directory.
    exit /b 1
)

echo Creating directories if they don't exist...
if not exist "..\..\builder-quantum-studio\client\services" mkdir "..\..\builder-quantum-studio\client\services"
if not exist "..\..\builder-quantum-studio\client\components\organlink" mkdir "..\..\builder-quantum-studio\client\components\organlink"

echo Copying API integration service to frontend project...
copy /Y "api-integration.js" "..\..\builder-quantum-studio\client\services\organlink-api.js"

echo Copying WebSocket service to frontend project...
copy /Y "websocket-service.js" "..\..\builder-quantum-studio\client\services\websocket-service.js"

echo Copying Notification service to frontend project...
copy /Y "notification-service.js" "..\..\builder-quantum-studio\client\services\notification-service.js"

echo Copying example component to frontend project...
copy /Y "example-component.jsx" "..\..\builder-quantum-studio\client\components\organlink\OrganLinkDashboard.jsx"

echo Copying proxy configuration to frontend project...
copy /Y "proxy-config.js" "..\..\builder-quantum-studio\proxy-config.js"

echo Updating Vite configuration...

REM Create a temporary file with the new Vite config content
echo import { defineConfig } from 'vite'; > temp-vite-config.ts
echo import react from '@vitejs/plugin-react'; >> temp-vite-config.ts
echo import proxyConfig from './proxy-config'; >> temp-vite-config.ts
echo. >> temp-vite-config.ts
echo export default defineConfig({ >> temp-vite-config.ts
echo   plugins: [react()], >> temp-vite-config.ts
echo   server: { >> temp-vite-config.ts
echo     port: 3000, >> temp-vite-config.ts
echo     proxy: proxyConfig >> temp-vite-config.ts
echo   } >> temp-vite-config.ts
echo }); >> temp-vite-config.ts

REM Copy the temporary file to the actual Vite config location
copy /Y "temp-vite-config.ts" "..\..\builder-quantum-studio\vite.config.ts"

REM Delete the temporary file
del temp-vite-config.ts

echo.
echo Integration setup complete!
echo.
echo To run the integrated application:
echo 1. Start the Spring Boot backend: cd ../.. ^&^& cd organlink ^&^& mvn spring-boot:run
echo 2. Start the React frontend: cd ../.. ^&^& cd builder-quantum-studio ^&^& npm run dev
echo 3. Access the application at http://localhost:8080
echo.
echo ===================================================