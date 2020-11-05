# Operator SDK: Go

#### 1. Create Operator

Generate a scaffold for the operator.

```
operator-sdk init --repo=github.com/leszko/hazelcast-operator
operator-sdk create api --version v1 --group=hazelcast --kind Hazelcast --resource=true --controller=true
```

Add the logic to `api/v1/hazelcast-types.go` and `controllers/hazelcast_controller.go`.

#### 2. Dockerize operator application and push it into Docker Hub

```
docker build -t leszko/hazelcast-operator:go . && docker push leszko/hazelcast-operator:go
```

#### 3. Create Hazelcast CRD (Custom Role Definition)

```
make install
```

#### 4. Deploy an operator

```
make deploy IMG=leszko/hazelcast-operator:go
```

Check that the operator is running with the following command.

```
kubectl logs deployment.apps/hazelcast-operator-controller-manager -n hazelcast-operator-system -c manager
```

#### 5. Create Hazelcast Resource

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

#### 6. Clean up

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
kustomize build config/default | kubectl delete -f -
```
