# Operator SDK: Helm Chart

#### 1. Create Operator

You need first to create Helm Chart as described [here](chart/README.md).

Then, you can generate the operator with the following commands.

```
operator-sdk init --plugins=helm
operator-sdk create api --group=hazelcast --version=v1 --helm-chart=./chart
```

#### 2. Dockerize operator application and push it into Docker Hub

```
docker build -t leszko/hazelcast-operator:helm . && docker push leszko/hazelcast-operator:helm
```

#### 3. Create Hazelcast CRD (Custom Role Definition)

```
make install
```

#### 4. Deploy an operator

```
make deploy IMG=leszko/hazelcast-operator:helm
```

Check that the operator is running with the following command.

```
kubectl logs deployment.apps/helm-controller-manager -n helm-system -c manager
```

#### 5. Create Hazelcast Resource

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

Note: If you want Hazelcast members themselves to form a cluster, you need to configure RBAC for Hazelcast: `kubectl apply -f https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml`.

#### 6. Clean up

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
make undeploy
```
