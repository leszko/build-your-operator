1. Generate Operator scaffold

```
operator-sdk init --plugins=ansible
operator-sdk create api --group hazelcast --version v1 --kind Hazelcast --generate-role
```

2. Add logic to create Deployment resources `Hazelcast` is created

Update `roles/hazelcast/tasks/main.yaml`

```
---
- name: start memcached
  community.kubernetes.k8s:
    definition:
      kind: Deployment
      apiVersion: apps/v1
      metadata:
        name: hazelcast
        namespace: '{{ ansible_operator_meta.namespace }}'
      spec:
        replicas: "{{size}}"
        selector:
          matchLabels:
            app: hazelcast
        template:
          metadata:
            labels:
              app: hazelcast
          spec:
            containers:
            - name: hazelcast
              image: "hazelcast/hazelcast:4.0"
```

Update `role/hazelcast/defaults/main.yaml`
```
---
size: 1
```

3. Build and Push the Operator

```
docker build -t leszko/hazelcast-operator:ansible . && docker push leszko/hazelcast-operator:ansible
```

4. Install Operator

Clean up `Makefile` and then run the following commands.

```
make install
make deploy IMG=leszko/hazelcast-operator:ansible
```

5. Check that the operator is running

```
kubectl logs deployment.apps/operator-ansible-controller-manager -n operator-ansible-system -c manager
```

6. Install resource (Hazelcast cluster)

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

6. Present that Hazelcast is running

7. Uninstall

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
make undeploy
```