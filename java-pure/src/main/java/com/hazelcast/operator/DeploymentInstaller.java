package com.hazelcast.operator;

import com.hazelcast.operator.cr.HazelcastResource;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class DeploymentInstaller {

    @Inject
    private KubernetesClient client;

    @Inject
    private HazelcastResourceCache cache;

    void onStartup(@Observes StartupEvent _ev) {
        new Thread(this::runWatch).start();
    }

    private void runWatch() {
        cache.listThenWatch(this::handleEvent);
    }

    private void handleEvent(Watcher.Action action, String uid) {
        try {
            HazelcastResource resource = cache.get(uid);
            if (resource == null) {
                return;
            }

            Predicate<Deployment> ownerRefMatches = deployments -> deployments.getMetadata().getOwnerReferences().stream()
                    .anyMatch(ownerReference -> ownerReference.getUid().equals(uid));


            List<Deployment> hazelcastDeployments = client.apps().deployments().list().getItems().stream()
                    .filter(ownerRefMatches)
                    .collect(toList());

            if (hazelcastDeployments.isEmpty()) {
                client.apps().deployments().create(newDeployment(resource));
            } else {
                for (Deployment deployment : hazelcastDeployments) {
                    setSize(deployment, resource);
                    client.apps().deployments().createOrReplace(deployment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Deployment newDeployment(HazelcastResource resource) {
        Deployment deployment = client.apps().deployments().load(getClass().getResourceAsStream("/deployment.yaml")).get();
        setSize(deployment, resource);
        deployment.getMetadata().getOwnerReferences().get(0).setUid(resource.getMetadata().getUid());
        deployment.getMetadata().getOwnerReferences().get(0).setName(resource.getMetadata().getName());
        return deployment;
    }

    private void setSize(Deployment deployment, HazelcastResource resource) {
        deployment.getSpec().setReplicas(resource.getSpec().getSize());
    }
}
