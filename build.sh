#!/bin/bash
APP_NAME='registry-editor'
clear &&
echo "=== clean ${APP_NAME} ===" &&
mvn clean &&
clear &&
echo "=== install ${APP_NAME} ===" &&
mvn install &&
clear &&
echo "=== ${APP_NAME} is successfully builded ==="
