#!/bin/sh
set -e

for ARGUMENT in "$@"
do
   KEY=$(echo "$ARGUMENT" | cut -f1 -d=)

   KEY_LENGTH=${#KEY}
   VALUE="${ARGUMENT:$KEY_LENGTH+1}"

   export "$KEY"="$VALUE"
done

if [ -z "$DB_PASSWORD" ];
then
    echo "$(date) - Missing mandatory arguments: DB_PASSWORD "
    exit 1
fi

CONTAINER_NAME='kalavithi-db-container'
IMAGE='kalavithi-db'

CID=$(docker ps -q -f status=running -f name=^/$CONTAINER_NAME$)
if [ ! "${CID}" ]; then
    if [ "$(docker ps -aq -f status=exited -f name=$CONTAINER_NAME)" ]; then
        # cleanup
        docker rm $CONTAINER_NAME
    fi
    # run your container
    docker run -d --name $CONTAINER_NAME -p 5432:5432 --env-file ./env.list -e POSTGRES_PASSWORD="$DB_PASSWORD" $IMAGE
fi
unset CID