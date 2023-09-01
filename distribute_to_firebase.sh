#!/bin/bash

# Define the Firebase project ID and distribution group ID
FIREBASE_PROJECT_ID="1:43735507988:android:f5f41e8de9650b4dc67301"

# Find all APK files recursively and distribute them to Firebase
find app/build/outputs/apk -type f -name "*.apk" | while read -r apk; do
    filename=$(basename -- "$apk")
    filename_noext="${filename%.*}"

    echo "$filename_noext"

    if [[ $filename_noext == *"development"* ]]; then
        firebase appdistribution:distribute "$apk" --app "$FIREBASE_PROJECT_ID" --groups development-testers &
    fi

    if [[ $filename_noext == *"staging"* ]]; then
        firebase appdistribution:distribute "$apk" --app "$FIREBASE_PROJECT_ID" --groups staging-testers &
    fi

    if [[ $filename_noext == *"master"* ]]; then
        firebase appdistribution:distribute "$apk" --app "$FIREBASE_PROJECT_ID" --groups master-testers &
    fi
done

# Wait for the distribution jobs to finish
wait

echo "All APKs distributed to Firebase!"