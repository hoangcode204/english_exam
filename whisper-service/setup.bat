@echo off
echo ================================
echo Whisper Service Setup Script
echo ================================
echo.

REM Check Python installation
python --version >nul 2>&1
if errorlevel 1 (
    py --version >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] Python is not installed or not in PATH
        echo Please install Python 3.11 or higher from https://www.python.org/
        pause
        exit /b 1
    ) else (
        echo [WARNING] Using 'py' launcher instead of 'python'
        set PYTHON_CMD=py
    )
) else (
    set PYTHON_CMD=python
)

echo [OK] Python is installed
echo.

REM Check if virtual environment exists
if exist venv (
    echo [INFO] Virtual environment already exists
) else (
    echo [INFO] Creating virtual environment...
    %PYTHON_CMD% -m venv venv
    if errorlevel 1 (
        echo [ERROR] Failed to create virtual environment
        pause
        exit /b 1
    )
    echo [OK] Virtual environment created
)
echo.

REM Activate virtual environment
echo [INFO] Activating virtual environment...
call venv\Scripts\activate.bat
if errorlevel 1 (
    echo [ERROR] Failed to activate virtual environment
    pause
    exit /b 1
)
echo.

REM Install requirements
echo [INFO] Installing dependencies (this may take several minutes)...
pip install -r requirements.txt
if errorlevel 1 (
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)
echo.
echo [OK] Dependencies installed successfully
echo.

REM Check ffmpeg
echo [INFO] Checking ffmpeg installation...
ffmpeg -version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] ffmpeg is not installed
    echo Please install ffmpeg from https://ffmpeg.org/download.html
    echo Or use: choco install ffmpeg
    echo.
) else (
    echo [OK] ffmpeg is installed
)
echo.

echo ================================
echo Setup completed successfully!
echo ================================
echo.
echo To start the service, run:
echo   venv\Scripts\activate
echo   python main.py
echo.
echo Or simply run:
echo   run_whisper.bat
echo.
pause
