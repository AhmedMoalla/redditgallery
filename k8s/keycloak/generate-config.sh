#!/bin/bash

kubectl create configmap keycloak-config --dry-run=client --from-file=realm.json -o yaml > keycloak-config.yaml