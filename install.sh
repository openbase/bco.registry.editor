#!/bin/bash
APP_NAME='registry-editor'
clear &&
echo "=== clean ${APP_NAME} ===" &&
mvn clean &&
clear &&
echo "=== install ${APP_NAME} ===" &&
mvn install &&
clear &&
echo "=== deploy ${APP_NAME} to ${prefix} ===" &&
mvn package jar:jar appassembler:assemble -DassembleDirectory=${prefix} &&
clear &&
echo "=== ${APP_NAME} is successfully installed to ${prefix} ==="
