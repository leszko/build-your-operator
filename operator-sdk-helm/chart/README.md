# Steps to create Helm Chart

1. Create a sample helm chart

```
helm create hazelcast
```

2. Change the image to `hazelcast/hazelcast:4.1-BETA-1`

3. Remove all unused parts in template and values

4. Install Helm Chart

```
helm install hazelcast .
```

5. Play with the Helm Chart

6. Uninstall the chart

```
helm uninstall hazelcast
```
