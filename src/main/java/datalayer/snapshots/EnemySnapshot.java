package datalayer.snapshots;

public record EnemySnapshot(
        String type,
        int x, int y,
        int maxHp, int curHp,
        int str, int dex, int hos
) {}