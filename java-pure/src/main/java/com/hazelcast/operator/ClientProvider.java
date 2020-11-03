package com.hazelcast.operator;

import com.hazelcast.operator.cr.HazelcastResource;
import com.hazelcast.operator.cr.HazelcastResourceDoneable;
import com.hazelcast.operator.cr.HazelcastResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientProvider {

    @Produces
    @Singleton
    @Named("namespace")
    private String findNamespace() throws IOException {
        return new String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace")));
    }

    @Produces
    @Singleton
    KubernetesClient newClient(@Named("namespace") String namespace) {
        return new DefaultKubernetesClient().inNamespace(namespace);
    }

    @Produces
    @Singleton
    NonNamespaceOperation<HazelcastResource, HazelcastResourceList, HazelcastResourceDoneable, Resource<HazelcastResource, HazelcastResourceDoneable>> makeCustomResourceClient(
            KubernetesClient defaultClient, @Named("namespace") String namespace) {

        KubernetesDeserializer.registerCustomKind("hazelcast.my.domain/v1", "Hazelcast", HazelcastResource.class);

        CustomResourceDefinition crd = defaultClient
                .customResourceDefinitions()
                .list()
                .getItems()
                .stream()
                .filter(d -> "hazelcasts.hazelcast.my.domain".equals(d.getMetadata().getName()))
                .findAny()
                .orElseThrow(
                        () -> new RuntimeException(
                                "Deployment error: Custom resource definition \"hazelcasts.hazelcast.my.domain\" not found."));

        return defaultClient
                .customResources(crd, HazelcastResource.class, HazelcastResourceList.class, HazelcastResourceDoneable.class)
                .inNamespace(namespace);
    }
}
