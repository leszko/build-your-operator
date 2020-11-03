package com.hazelcast.operator;

import com.hazelcast.operator.cr.HazelcastResource;
import com.hazelcast.operator.cr.HazelcastResourceDoneable;
import com.hazelcast.operator.cr.HazelcastResourceList;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

@ApplicationScoped
public class HazelcastResourceCache {

    private final Map<String, HazelcastResource> cache = new ConcurrentHashMap<>();

    @Inject
    private NonNamespaceOperation<HazelcastResource, HazelcastResourceList, HazelcastResourceDoneable, Resource<HazelcastResource, HazelcastResourceDoneable>> crClient;

    private Executor executor = Executors.newSingleThreadExecutor();

    public HazelcastResource get(String uid) {
        return cache.get(uid);
    }

    public void listThenWatch(BiConsumer<Watcher.Action, String> callback) {

        try {
            // list
            crClient
                    .list()
                    .getItems()
                    .forEach(resource -> {
                                cache.put(resource.getMetadata().getUid(), resource);
                                String uid = resource.getMetadata().getUid();
                                executor.execute(() -> callback.accept(Watcher.Action.ADDED, uid));
                            }
                    );

            // watch
            crClient.watch(new Watcher<HazelcastResource>() {
                @Override
                public void eventReceived(Action action, HazelcastResource resource) {
                    try {
                        String uid = resource.getMetadata().getUid();
                        if (cache.containsKey(uid)) {
                            int knownResourceVersion = Integer.parseInt(cache.get(uid).getMetadata().getResourceVersion());
                            int receivedResourceVersion = Integer.parseInt(resource.getMetadata().getResourceVersion());
                            if (knownResourceVersion > receivedResourceVersion) {
                                return;
                            }
                        }
                        System.out.println("received " + action + " for resource " + resource);
                        if (action == Action.ADDED || action == Action.MODIFIED) {
                            cache.put(uid, resource);
                        } else if (action == Action.DELETED) {
                            cache.remove(uid);
                        } else {
                            System.err.println("Received unexpected " + action + " event for " + resource);
                            System.exit(-1);
                        }
                        executor.execute(() -> callback.accept(action, uid));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }

                @Override
                public void onClose(KubernetesClientException cause) {
                    cause.printStackTrace();
                    System.exit(-1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
