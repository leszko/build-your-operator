1. Generate Operator from the Helm Chart

```
operator-sdk init --plugins=helm
operator-sdk create api --helm-chart=../helm-chart/hazelcast
```

2. Build and Push the Operator

```
docker build -t leszko/hazelcast-operator:helm . && docker push leszko/hazelcast-operator:helm
```

3. Install Operator

Clean up `Makefile` and then run the following commands.

```
make install
make deploy IMG=leszko/hazelcast-operator:helm
```

4. Check that the operator is running.

```
kubectl logs deployment.apps/operator-helm-controller-manager -n operator-helm-system -c manager
```

5. Install resource (Hazelcast cluster)

```
kubectl apply -f config/samples/charts_v1alpha1_hazelcast.yaml
```

6. Present that Hazelcast is running

7. Uninstall

```
kubectl delete -f config/samples/charts_v1alpha1_hazelcast.yaml
make undeploy
```