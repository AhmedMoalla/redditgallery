#!/bin/bash

kubectl create configmap mongo-env --dry-run=client --from-env-file=.env -o yaml > mongo-env.yaml
kubectl create configmap mongo-config --dry-run=client \
                                      --from-file=mongod.conf \
                                      --from-file=./initdb.d/create-app-user.sh \
                                      -o yaml > mongo-config.yaml