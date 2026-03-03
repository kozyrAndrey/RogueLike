package datalayer.dto;

public record SavedSessionDTO(
        long savedAtMillis,
        LevelStateDTO currentLevelState,
        datalayer.snapshots.RunStatsSnapshot runStats,
        int completedLevel
) {}
