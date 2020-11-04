package com.hazelcast.operator.cr;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class HazelcastResourceDoneable extends CustomResourceDoneable<HazelcastResource> {

    public HazelcastResourceDoneable(HazelcastResource resource, Function<HazelcastResource, HazelcastResource> function) {
        super(resource, function);
    }
}
