# Operator SDK: Ansible

#### 1. Create Operator

Generate scaffold with the following command.

```
operator-sdk init --plugins=ansible
operator-sdk create api --group hazelcast --version v1 --kind Hazelcast --generate-role
```

Then, you can add logic to `roles/hazelcast/tasks/main.yaml` and `role/hazelcast/defaults/main.yaml`.

#### 2. Dockerize operator application and push it into Docker Hub

```
docker build -t leszko/hazelcast-operator:ansible . && docker push leszko/hazelcast-operator:ansible
```

#### 3. Create Hazelcast CRD (Custom Role Definition)

```
make install
```

#### 4. Deploy an operator

```
make deploy IMG=leszko/hazelcast-operator:ansible
```

Check that the operator is running with the following command.

```
kubectl logs deployment.apps/operator-ansible-controller-manager -n operator-ansible-system -c manager
```

#### 5. Create Hazelcast Resource

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

#### 6. Clean up

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
make undeploy
```