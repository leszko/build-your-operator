
# Java

#### 1. Create operator

Java Operator is created as a Java Quarkus project. You can buld it either as a standard Docker image or a Native Docker image using GraalVM.

##### 1.1. Standard build
```
mvn package
```

##### 1.2 Native build
```
./mvnw package -Pnative -DskipTests -Dnative-image.docker-build=true
```

#### 2. Dockerize operator application and push it into Docker Hub

##### 2.1. Standard build
```
docker build -f src/main/docker/Dockerfile.jvm -t leszko/hazelcast-operator:java . && docker push leszko/hazelcast-operator:java
```

##### 2.2. Native build
```
docker build -f src/main/docker/Dockerfile.native -t leszko/hazelcast-operator:java-native . && docker push leszko/hazelcast-operator:java-native
```

#### 3. Create Hazelcast CRD (Custom Role Definition) and RBAC (Cluster Role and Cluster Role Binding)
```
kubectl apply -f hazelcast.crd.yaml
kubectl apply -f role.yaml
kubectl apply -f role_binding.yaml
```

#### 4. Deploy an operator

##### 4.1. Standard build
```
kubectl apply -f operator.yaml
```

##### 4.2. Native build
```
kubectl apply -f operator.deployment.native.yaml
```

#### 5. Create Hazelcast Resource
```
kubectl apply -f hazelcast.yaml
```

#### 6. Clean up
```
kubectl delete -f hazelcast.yaml
kubectl delete -f operator.yaml
kubectl delete -f role_binding.yaml
kubectl delete -f role.yaml
kubectl delete -f hazelcast.crd.yaml
```


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
kubectl delete -f operator.clusterrole.yaml
kubectl delete -f operator.serviceaccount.yaml
kubectl delete -f operator.clusterrolebinding.yaml
kubectl delete -f hazelcast.crd.yaml
```
