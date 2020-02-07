package net.glowstone.redstone_transformer.generation;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ManagerBlockDataDetails {
    private final String materialName;
    private final String blockDataSimpleName;
    private final Map<Integer, Map<String, String>> blockIds;
    private final Set<ManagerStateReportDetails> stateReportDetails;

    public ManagerBlockDataDetails(String materialName, String blockDataSimpleName, Map<Integer, Map<String, String>> blockIds, Set<ManagerStateReportDetails> stateReportDetails) {
        this.materialName = materialName;
        this.blockDataSimpleName = blockDataSimpleName;
        this.blockIds = blockIds;
        this.stateReportDetails = stateReportDetails;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getBlockDataSimpleName() {
        return blockDataSimpleName;
    }

    public Map<Integer, Map<String, String>> getBlockIds() {
        return blockIds;
    }

    public Set<ManagerStateReportDetails> getStateReportDetails() {
        return stateReportDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerBlockDataDetails that = (ManagerBlockDataDetails) o;
        return Objects.equals(materialName, that.materialName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialName);
    }
}