# Build Your Operator with the Right Tool!

This repository presents a tutorial to build a basic sample operator using different techniques.

## Assumptions

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

All other parameters are hardcoded, for example, Hazelcast Docker: `hazelcast/hazelcast:4.0`.
