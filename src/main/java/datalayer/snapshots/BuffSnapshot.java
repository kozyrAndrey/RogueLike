package datalayer.snapshots;

public record BuffSnapshot(String type,
                           int value,
                           int remainingTime) {
}
