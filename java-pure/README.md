
1. Apply all roles and CRDs

```
kubectl apply -f operator.clusterrole.yaml
kubectl apply -f operator.serviceaccount.yaml
kubectl apply -f operator.clusterrolebinding.yaml
kubectl apply -f hazelcast.crd.yaml
```

2.1. Build Operator
```
mvn package
docker build -f src/main/docker/Dockerfile.jvm -t leszko/hazelcast-operator:java . && docker push leszko/hazelcast-operator:java
kubectl apply -f operator.deployment.yaml
```

2.2. Build Operator (Native)

```
./mvnw package -Pnative -DskipTests -Dnative-image.docker-build=true
docker build -f src/main/docker/Dockerfile.native -t leszko/hazelcast-operator:java-native . && docker push leszko/hazelcast-operator:java-native
kubectl apply -f operator.deployment.native.yaml
```

3. Create Hazelcast resource

```
kubectl apply -f hazelcast.yaml
```

4. Clean up

```
kubectl delete -f hazelcast.yaml
kubectl delete -f operator.deployment.yaml
```