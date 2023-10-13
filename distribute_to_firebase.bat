@echo off
setlocal enabledelayedexpansion

REM Define the Firebase project ID and distribution group ID
set FIREBASE_PROJECT_ID=1:43735507988:android:f5f41e8de9650b4dc67301

REM Loop through all built APKs and distribute them to Firebase
for /R app\build\outputs\apk %%f in (*.apk) do (
    echo "%%~nf"

    echo "%%~nf" | findstr /R /C:"development" >NUL
    IF !ERRORLEVEL! EQU 0 (
        start "Distributing APK" cmd /c firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups development-testers
    )

REM      echo "%%~nf" | findstr /R /C:"staging" >NUL
REM      IF !ERRORLEVEL! EQU 0 (
REM          start "Distributing APK" cmd /c firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups staging-testers
REM      )

REM      echo "%%~nf" | findstr /R /C:"master" > NUL
REM       IF !ERRORLEVEL! EQU 0 (
REM           start "Distributing APK" cmd /c firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups master-testers
REM       )
)

:wait_for_jobs
timeout /t 5 >nul
tasklist /v | find "Distributing APK" >nul && goto :wait_for_jobs

echo All APKs distributed to Firebase!


