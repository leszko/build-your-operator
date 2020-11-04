# KOPF (Kubernetes Operator Pythonic Framework)

#### 1. Create operator

The whole logic of the operator is included in the file `operator.py`.

#### 2. Dockerize operator application and push it into Docker Hub
```
docker build -t leszko/hazelcast-operator:kopf . && docker push leszko/hazelcast-operator:kopf
```

#### 3. Create Hazelcast CRD (Custom Role Definition) and RBAC (Cluster Role and Cluster Role Binding)
```
kubectl apply -f hazelcast.crd.yaml
kubectl apply -f role.yaml
kubectl apply -f role_binding.yaml
```

#### 4. Deploy an operator
```
kubectl apply -f operator.yaml
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