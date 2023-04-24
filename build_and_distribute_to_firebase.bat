@echo off
setlocal

REM Define the Firebase project ID and distribution group ID
set FIREBASE_PROJECT_ID=1:43735507988:android:f5f41e8de9650b4dc67301

REM Set the distribution group ID for each flavor
set DEVELOPMENT_GROUP_ID=development-testers
set STAGING_GROUP_ID=staging-testers
set PRODUCTION_GROUP_ID=production-testers

REM Loop through all project flavors and build the APKs
for %%f in (development staging production) do (
    echo Building APK for %%f...
    gradlew.bat assemble%%fDebug
    REM gradlew.bat assemble%%fRelease
)

REM Loop through all built APKs and distribute them to Firebase
for /R app\build\outputs\apk %%f in (*.apk) do (
    set DISTRIBUTION_GROUP=

    if "%%~nf"=="app-development-debug" (
        set DISTRIBUTION_GROUP=%DEVELOPMENT_GROUP_ID%
    ) else if "%%~nf"=="app-staging-debug" (
        set DISTRIBUTION_GROUP=%STAGING_GROUP_ID%
    ) else if "%%~nf"=="app-production-debug" (
        set DISTRIBUTION_GROUP=%PRODUCTION_GROUP_ID%
    )

    if not "%DISTRIBUTION_GROUP%"=="" (
        echo Distributing APK %%~nxf to group %DISTRIBUTION_GROUP%...
        firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups %DISTRIBUTION_GROUP% --debug
    )
)

echo All APKs distributed to Firebase!
