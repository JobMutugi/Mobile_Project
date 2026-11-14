@echo off
setlocal
set GRADLE_VERSION=8.13
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set BASE_DIR=%GRADLE_USER_HOME%\manual-wrapper
set INSTALL_DIR=%BASE_DIR%\gradle-%GRADLE_VERSION%
set ZIP_FILE=%BASE_DIR%\gradle-%GRADLE_VERSION%-bin.zip
set GRADLE_BIN=%INSTALL_DIR%\bin\gradle.bat
if exist "%GRADLE_BIN%" goto run_gradle
if not exist "%BASE_DIR%" mkdir "%BASE_DIR%"
if not exist "%ZIP_FILE%" (
  echo Downloading official Gradle %GRADLE_VERSION%. Internet access is required for the first run.
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP_FILE%'"
  if errorlevel 1 exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP_FILE%' -DestinationPath '%BASE_DIR%' -Force"
if errorlevel 1 exit /b 1
:run_gradle
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
