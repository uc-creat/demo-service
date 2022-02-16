#!/bin/sh
set -e

cd ../..
echo "working directory ${PWD}"

echo "building docker image"
docker build -t kalavithi-service -f ./docker/app/Dockerfile .