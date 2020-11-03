package com.hazelcast.operator.cr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class HazelcastResourceSpec {

    @JsonProperty("size")
    private Integer size;

    public Integer getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "size=" + size;
    }
}