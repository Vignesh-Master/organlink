@echo off
REM Build script for OrganLink project when Maven is not available

echo ========================================
echo OrganLink Project Build Script
echo ========================================

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21+ and add it to your PATH
    pause
    exit /b 1
)

echo Java is available:
java -version

echo.
echo ========================================
echo Build Options:
echo ========================================
echo 1. Use IDE (VS Code/IntelliJ) - RECOMMENDED
echo 2. Install Maven manually
echo 3. Use online Maven repository
echo 4. Build with Gradle (alternative)
echo.

echo RECOMMENDED APPROACH:
echo.
echo For VS Code:
echo 1. Install "Extension Pack for Java"
echo 2. Open Command Palette (Ctrl+Shift+P)
echo 3. Type "Java: Rebuild Projects"
echo 4. Select OrganLink project
echo.
echo For IntelliJ IDEA:
echo 1. Right-click on pom.xml
echo 2. Select "Maven" -^> "Reload project"
echo 3. Build -^> Build Project (Ctrl+F9)
echo.

echo ========================================
echo Manual Maven Installation:
echo ========================================
echo 1. Download from: https://maven.apache.org/download.cgi
echo 2. Extract to C:\Program Files\Apache\maven
echo 3. Add C:\Program Files\Apache\maven\bin to PATH
echo 4. Restart terminal and run: mvn --version
echo.

echo ========================================
echo Alternative: Use Gradle
echo ========================================
echo We can convert this project to use Gradle instead of Maven
echo Gradle has a wrapper that doesn't require installation
echo.

pause
