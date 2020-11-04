# Build Your Operator with the Right Tool!

This repository presents a tutorial to build a basic sample operator using different techniques.

## What You'll Build

Our Operator is responsible for creating a Hazelcast cluster from the following resource definition `hazelcast.yaml`:

```
apiVersion: hazelcast.my.domain/v1
kind: Hazelcast
metadata:
  name: hazelcast-sample
spec:
  size: 1
```

The only parameter to configure is `size` which decides on the number of running Hazelcast Pods.

All other parameters are hardcoded, for example, Hazelcast Docker: `hazelcast/hazelcast:4.1`.

## How You'll Build

No matter of the operator type, you'll always proceed with the steps below. Note that only the first point differs between different ways of creating operators.

1. Create operator (using different techniques)
2. Dockerize operator application and push it into Docker Hub
```
docker build -t <username>/hazelcast-operator:<type> . && docker push <username>/hazelcast-operator:<type>
```

3. Create Hazelcast CRD (Custom Role Definition) and RBAC (Cluster Role and Cluster Role Binding)
```
kubectl apply -f hazelcast.crd.yaml
kubectl apply -f role.yaml
kubectl apply -f role_binding.yaml
```

4. Deploy an operator
```
kubectl apply -f operator.yaml
```

5. Create Hazelcast Resource
```
kubectl apply -f hazelcast.yaml
```