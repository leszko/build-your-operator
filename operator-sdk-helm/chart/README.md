# Steps to create Helm Chart

#### 1. Create a sample helm chart

```
helm create hazelcast
```

#### 2. Update the chart

* change the image to `hazelcast/hazelcast:4.1`
* remove all unused parts in template and values

#### 3. Install Helm Chart

```
helm install hazelcast .
```

#### 4. Clean up

```
helm uninstall hazelcast
```
