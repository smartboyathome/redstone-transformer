package net.glowstone.datapack.loader.model.external.advancement.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class ChangedDimensionConditions implements Conditions {
    public static final String TYPE_ID = "minecraft:changed_dimension";

    private final Optional<String> from;
    private final Optional<String> to;

    @JsonCreator
    public ChangedDimensionConditions(
        @JsonProperty("from") Optional<String> from,
        @JsonProperty("to") Optional<String> to) {
        this.from = from;
        this.to = to;
    }

    public Optional<String> getFrom() {
        return from;
    }

    public Optional<String> getTo() {
        return to;
    }
}
