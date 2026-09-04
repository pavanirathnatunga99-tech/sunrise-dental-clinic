@echo off
setlocal
cd /d "%~dp0"

where node >nul 2>nul
if errorlevel 1 (
  echo Node.js is not installed or is not available in PATH.
  echo Install Node.js 20 or newer from https://nodejs.org/ and try again.
  pause
  exit /b 1
)

if not exist "frontend\node_modules\vite\bin\vite.js" (
  echo Installing frontend dependencies. This is required only once...
  call npm.cmd install --prefix frontend
  if errorlevel 1 (
    echo.
    echo Dependency installation failed. Check your internet connection and npm installation.
    pause
    exit /b 1
  )
)

echo Starting Sunrise Dental Clinic...
start "Sunrise Dental - API" cmd /k "cd /d ""%~dp0"" && node dev-api.mjs"
start "Sunrise Dental - Frontend" cmd /k "cd /d ""%~dp0frontend"" && node start-dev.mjs"

echo.
echo Keep both new terminal windows open.
echo Open http://localhost:5173 after a few seconds.
timeout /t 4 /nobreak >nul
start "" "http://localhost:5173"
endlocal
