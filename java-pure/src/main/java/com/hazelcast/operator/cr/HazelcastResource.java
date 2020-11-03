package com.hazelcast.operator.cr;

import io.fabric8.kubernetes.client.CustomResource;

public class HazelcastResource extends CustomResource {

    private HazelcastResourceSpec spec;

    public HazelcastResourceSpec getSpec() {
        return spec;
    }

    public void setSpec(HazelcastResourceSpec spec) {
        this.spec = spec;
    }

    @Override
    public String toString() {
        String name = getMetadata() != null ? getMetadata().getName() : "unknown";
        String version = getMetadata() != null ? getMetadata().getResourceVersion() : "unknown";
        return "name=" + name + " version=" + version + " value=" + spec;
    }
}
