#!/bin/sh

./docker-build-db.sh
./docker-build-model.sh
./docker-build-mps.sh
./docker-build-base.sh
./docker-build-ui.sh
./docker-build-proxy.sh
./docker-build-uiproxy.sh
