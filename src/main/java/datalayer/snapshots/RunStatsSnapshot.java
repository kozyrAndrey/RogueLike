package datalayer.snapshots;

public record RunStatsSnapshot(
        int treasuresCollected,
        int enemiesDefeated,
        int foodEaten,
        int elixirsUsed,
        int scrollsRead,
        int hitsDealt,
        int hitsTaken,
        int cellsTraveled
) {}
