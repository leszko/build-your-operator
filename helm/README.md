1. Create Helm Chart

2. Generate Operator from the Helm Chart

```
operator-sdk init --plugins=helm
operator-sdk create api --group=hazelcast --version=v1 --helm-chart=./chart
```

3. Build and Push the Operator

```
docker build -t leszko/hazelcast-operator:helm . && docker push leszko/hazelcast-operator:helm
```

4. Install Operator

Clean up `Makefile` and then run the following commands.

```
make install
make deploy IMG=leszko/hazelcast-operator:helm
```

5. Check that the operator is running

```
kubectl logs deployment.apps/helm-controller-manager -n helm-system -c manager
```

6. Install resource (Hazelcast cluster)

```
kubectl apply -f config/samples/hazelcast_v1_hazelcast.yaml
```

7. Present that Hazelcast is running and scaling up

8. Uninstall

```
kubectl delete -f config/samples/hazelcast_v1_hazelcast.yaml
make undeploy
```