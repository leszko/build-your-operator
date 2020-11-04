import kopf
import pykube
import yaml

@kopf.on.create('hazelcast.my.domain', 'v1', 'hazelcasts')
def create_fn(spec, **kwargs):
    doc = create_deployment(spec)
    kopf.adopt(doc)

    api = pykube.HTTPClient(pykube.KubeConfig.from_env())
    deployment = pykube.Deployment(api, doc)
    deployment.create()

    api.session.close()

    return {'children': [deployment.metadata['uid']]}


@kopf.on.update('hazelcast.my.domain', 'v1', 'hazelcasts')
def update_fn(spec, **kwargs):
    api = pykube.HTTPClient(pykube.KubeConfig.from_env())
    deployment = pykube.Deployment.objects(api).get(name="hazelcast")
    deployment.replicas = spec.get('size', 1)
    deployment.update()
    
    api.session.close()

    return {'children': [deployment.metadata['uid']]}

def create_deployment(spec):
    return yaml.safe_load(f"""
        apiVersion: apps/v1
        kind: Deployment
        metadata:
          name: hazelcast
        spec:
          replicas: {spec.get('size', 1)}
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
                  image: "hazelcast/hazelcast:4.1-BETA-1"
    """)

