package model.domain;

public class RunStats {
    private int treasuresCollected;
    private int enemiesDefeated;
    private int foodEaten;
    private int elixirsUsed;
    private int scrollsRead;
    private int hitsDealt;
    private int hitsTaken;
    private int cellsTraveled;

    public int getTreasuresCollected() { return treasuresCollected; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getFoodEaten() { return foodEaten; }
    public int getElixirsUsed() { return elixirsUsed; }
    public int getScrollsRead() { return scrollsRead; }
    public int getHitsDealt() { return hitsDealt; }
    public int getHitsTaken() { return hitsTaken; }
    public int getCellsTraveled() { return cellsTraveled; }

    public RunStats() {
    }

    public RunStats(int treasuresCollected, int enemiesDefeated, int foodEaten, int elixirsUsed,
                    int scrollsRead, int hitsDealt, int hitsTaken, int cellsTraveled) {
        this.treasuresCollected = treasuresCollected;
        this.enemiesDefeated = enemiesDefeated;
        this.foodEaten = foodEaten;
        this.elixirsUsed = elixirsUsed;
        this.scrollsRead = scrollsRead;
        this.hitsDealt = hitsDealt;
        this.hitsTaken = hitsTaken;
        this.cellsTraveled = cellsTraveled;
    }

    public void incrementTreasuresCollected(int gold) { treasuresCollected += gold; }
    public void incrementEnemiesDefeated() { enemiesDefeated++; }
    public void incrementFoodEaten() { foodEaten++; }
    public void incrementElixirsUsed() { elixirsUsed++; }
    public void incrementScrollsRead() { scrollsRead++; }
    public void incrementHitsDealt() { hitsDealt++; }
    public void incrementHitsTaken() { hitsTaken++; }
    public void incrementCellsTraveled() { cellsTraveled++; }
}
