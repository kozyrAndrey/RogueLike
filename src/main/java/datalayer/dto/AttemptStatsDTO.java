package datalayer.dto;

public record AttemptStatsDTO(
        String name,
        int treasuresCollected,
        int reachedLevel,
        int enemiesDefeated,
        int foodEaten,
        int elixirsUsed,
        int scrollsRead,
        int hitsDealt,
        int hitsTaken,
        int cellsTraveled,
        boolean won
) {}
