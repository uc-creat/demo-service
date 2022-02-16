#!/bin/sh
set -e

cd ../..
echo "working directory ${PWD}"

echo "building executable jar"
./gradlew clean bootJar

mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

echo "building docker image"

docker build -t kalavithi-service -f ./docker/app/Dockerfile .

