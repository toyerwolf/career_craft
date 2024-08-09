#!/bin/bash

# Убедитесь, что скрипт выполняется из корневой директории проекта

if [ -f "build.gradle" ]; then
    echo "Building with Gradle..."
    ./gradlew clean build
else
    echo "No recognized build file found!"
    exit 1
fi