@echo off
echo Building and running Smart Care Services...
echo.

REM Check if target directory exists, if not create it
if not exist "target\classes" mkdir target\classes

REM Compile Java files
echo Compiling Java files...
javac -d target\classes -cp "target\lib\*" src\main\java\com\smartcare\*.java src\main\java\com\smartcare\**\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo To run the application, you need Maven installed.
echo Please install Maven and run: mvn spring-boot:run
echo.
echo Project structure created successfully!
echo Check the following:
echo - Authentication endpoints at /api/v1/auth/*
echo - Doctor search at /api/v1/doctors/search
echo - Appointment booking at /api/v1/appointments/*
echo - Medication management at /api/v1/medications/*
echo - User profile at /api/v1/profile/*
echo.
echo API Documentation will be available at: http://localhost:8080/swagger-ui.html
echo H2 Database Console at: http://localhost:8080/h2-console
echo.
pause
