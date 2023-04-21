@echo off
setlocal

REM Define the Firebase project ID and distribution group ID
set FIREBASE_PROJECT_ID=1:43735507988:android:f5f41e8de9650b4dc67301
set DISTRIBUTION_GROUP_ID=internal


REM Loop through all project flavors and build the APKs
for %%f in (development staging production) do (
    echo Building APK for %%f...
    gradlew.bat assemble%%fDebug
    REM gradlew.bat assemble%%fRelease
)

REM Loop through all built APKs and distribute them to Firebase
for /R app\build\outputs\apk %%f in (*.apk) do (
    echo Distributing APK %%~nxf...
    firebase appdistribution:distribute "%%~dpnxf" --app %FIREBASE_PROJECT_ID% --groups %DISTRIBUTION_GROUP_ID% --debug
)

echo All APKs distributed to Firebase!