#!/bin/bash
APP_NAME='registry-editor'
clear &&
echo "=== clean ${APP_NAME} ===" &&
mvn clean &&
clear &&
echo "=== install ${APP_NAME} ===" &&
mvn install &&
clear &&
echo "=== run ${APP_NAME} to ${prefix} ===" &&
./target/appassembler/bin/${APP_NAME}
