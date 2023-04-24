@echo off
setlocal

REM Loop through all project flavors and build the APKs
for %%f in (development staging production) do (
    echo Building APK for %%f...
    gradlew.bat assemble%%fDebug
    REM gradlew.bat assemble%%fRelease
)
