#!/bin/bash

docker-compose -f src/main/docker/keycloak.yml up -d&
docker-compose -f src/main/docker/elasticsearch.yml up -d&
docker-compose -f src/main/docker/postgresql.yml up -d&
sleep 20
./mvnw
