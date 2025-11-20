@echo off
echo Starting Whisper Service...
echo.

REM Check if virtual environment exists
if not exist venv (
    echo [ERROR] Virtual environment not found!
    echo Please run setup.bat first
    pause
    exit /b 1
)

REM Activate virtual environment
call venv\Scripts\activate.bat

REM Check if activation was successful
if errorlevel 1 (
    echo [ERROR] Failed to activate virtual environment
    pause
    exit /b 1
)

REM Run the service
echo.
echo Whisper service is starting on http://localhost:8000
echo Press Ctrl+C to stop the service
echo.
python main.py

pause
