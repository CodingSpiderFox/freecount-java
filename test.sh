#!/bin/bash

docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
mvn verify mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
newman src/test/postman.json
