#!/bin/bash

# Loop through all project flavors and build the APKs
for flavor in development staging master; do
    echo "Building APK for $flavor..."
    ./gradlew assemble${flavor}Debug
    # Uncomment the following line to build release APKs
    # ./gradlew assemble${flavor}Release
done