@echo off
setlocal enabledelayedexpansion

REM Define the Firebase project ID and distribution group ID
set FIREBASE_PROJECT_ID=1:43735507988:android:f5f41e8de9650b4dc67301

REM Set the distribution group ID for each flavor
set DEVELOPMENT_GROUP_ID=development-testers
set STAGING_GROUP_ID=staging-testers
set PRODUCTION_GROUP_ID=production-testers

REM Loop through all project flavors and build the APKs
REM for %%f in (development staging production) do (
REM     echo Building APK for %%f...
REM     gradlew.bat assemble%%fDebug
REM     REM gradlew.bat assemble%%fRelease
REM )

REM Loop through all built APKs and distribute them to Firebase
for /R app\build\outputs\apk %%f in (*.apk) do (
    set DISTRIBUTION_GROUP=
    echo "%%~nf"
    echo "%%~nf" | findstr /R /C:"development" >NUL
    IF !ERRORLEVEL! EQU 0 (
        echo Distributing APK %%~nxf to group development-testers...
        firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups development-testers --debug
    )

    echo "%%~nf" | findstr /R /C:"staging" >NUL
    IF !ERRORLEVEL! EQU 0 (
        echo Distributing APK %%~nxf to group staging-testers...
        firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups staging-testers --debug
    )

    echo "%%~nf" | findstr /R /C:"production" > NUL
    IF !ERRORLEVEL! EQU 0 (
        echo Distributing APK %%~nxf to group production-testers...
        firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups production-testers --debug
    )

)

echo All APKs distributed to Firebase!


