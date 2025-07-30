@echo off
echo ========================================
echo Maven Installation Verification
echo ========================================

echo Checking if Maven is installed...
mvn --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ SUCCESS: Maven is installed!
    echo.
    mvn --version
    echo.
    echo ========================================
    echo Ready to build OrganLink project!
    echo ========================================
    echo.
    echo Available Maven commands:
    echo   mvn clean compile    - Clean and compile
    echo   mvn clean package    - Build JAR file
    echo   mvn spring-boot:run  - Run the application
    echo   mvn test            - Run tests
    echo.
) else (
    echo ❌ ERROR: Maven is not found in PATH
    echo.
    echo Please follow these steps:
    echo.
    echo 1. Download Maven from: https://maven.apache.org/download.cgi
    echo 2. Extract to: C:\Program Files\Apache\maven
    echo 3. Add to PATH: C:\Program Files\Apache\maven\bin
    echo 4. Restart terminal and run this script again
    echo.
    echo Current PATH contains:
    echo %PATH%
)

echo.
echo Java version:
java -version

pause
