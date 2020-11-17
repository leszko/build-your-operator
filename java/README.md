
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

Note: If you want Hazelcast members themselves to form a cluster, you need to configure RBAC for Hazelcast: `kubectl apply -f https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml`.

#### 6. Clean up
```
kubectl delete -f hazelcast.yaml
kubectl delete -f operator.yaml
kubectl delete -f role_binding.yaml
kubectl delete -f role.yaml
kubectl delete -f hazelcast.crd.yaml
```
