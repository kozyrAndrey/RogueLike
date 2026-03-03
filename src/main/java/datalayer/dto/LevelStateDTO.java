package datalayer.dto;

import datalayer.snapshots.*;

import java.util.List;

public record LevelStateDTO(
        int levelNumber,
        int[][] field,
        boolean[][] explored,
        HeroSnapshot hero,
        List<EnemySnapshot> enemies,
        List<ItemSnapshot> items,
        List<DoorSnapshot> doors,
        ExitSnapshot exit,
        int balancePrevDifficulty,
        int balanceMaxHpOnStart,
        int balanceStartValue
) {}
