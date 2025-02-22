#!/bin/sh

TAG=$( ./modelix-version.sh )

docker build --no-cache -f Dockerfile-ui -t modelix/modelix-ui .

docker tag modelix/modelix-ui:latest "modelix/modelix-ui:${TAG}"
sed -i.bak -E "s/(image:.*:).*/\1${TAG}/" kubernetes_deprecated/common/ui-deployment.yaml
rm kubernetes_deprecated/common/ui-deployment.yaml.bak
