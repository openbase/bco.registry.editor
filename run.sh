#!/bin/bash
APP_NAME='registry-editor'
clear &&
echo "=== run ${APP_NAME} to ${prefix} ===" &&
./target/appassembler/bin/${APP_NAME} $@
