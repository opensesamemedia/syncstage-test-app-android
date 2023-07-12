@echo off
setlocal

REM Loop through all project flavors and build the APKs
REM for %%f in (development staging production) do (
for %%f in (development) do (
    echo Building APK for %%f...
    gradlew.bat assemble%%fDebug
    REM gradlew.bat assemble%%fRelease
)
