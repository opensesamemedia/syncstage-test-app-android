@echo off
setlocal

REM Define the Firebase project ID and distribution group ID
set FIREBASE_PROJECT_ID=your_firebase_project_id
set DISTRIBUTION_GROUP_ID=your_firebase_distribution_group_id

REM Loop through all project flavors and build the APKs
for %%f in (development staging production) do (
    echo Building APK for %%f...
    gradlew.bat assemble%%fDebug
    REM gradlew.bat assemble%%fRelease
)

REM Loop through all built APKs and distribute them to Firebase
for /R app\build\outputs\apk do (
    if "%%~xf"==".apk" (
        echo Distributing APK %%~nxf...
        firebase appdistribution:distribute "%%~dpnxf" --app syncstagetestappandroid
    )
)

echo All APKs distributed to Firebase!
