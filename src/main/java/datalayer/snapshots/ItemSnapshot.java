package datalayer.snapshots;

public record ItemSnapshot(
        String type,
        int x, int y,
        int value,
        String boostType,
        String accessLevel,
        String name
) {}
