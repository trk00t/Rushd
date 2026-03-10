@echo off
setlocal

set GRADLE_VERSION=8.4
set GRADLE_DIST=gradle-%GRADLE_VERSION%-bin
set GRADLE_URL=https://services.gradle.org/distributions/%GRADLE_DIST%.zip
set GRADLE_HOME=%USERPROFILE%\.gradle\wrapper\dists\%GRADLE_DIST%

if exist "%GRADLE_HOME%\gradle-%GRADLE_VERSION%\bin\gradle.bat" (
    echo Using cached Gradle %GRADLE_VERSION%
    call "%GRADLE_HOME%\gradle-%GRADLE_VERSION%\bin\gradle.bat" %*
    goto :eof
)

echo Downloading Gradle %GRADLE_VERSION%...
mkdir "%GRADLE_HOME%" 2>nul

powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%GRADLE_URL%' -OutFile '%GRADLE_HOME%\%GRADLE_DIST%.zip' }"

if not exist "%GRADLE_HOME%\%GRADLE_DIST%.zip" (
    echo ERROR: Failed to download Gradle. Check your internet connection.
    exit /b 1
)

echo Extracting Gradle...
powershell -Command "Expand-Archive -Path '%GRADLE_HOME%\%GRADLE_DIST%.zip' -DestinationPath '%GRADLE_HOME%' -Force"

call "%GRADLE_HOME%\gradle-%GRADLE_VERSION%\bin\gradle.bat" %*
