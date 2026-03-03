package model.domain;

import java.util.List;

public record GameState(int[][] field,
                        FogState[][] fog,
                        int numberOfLevel,
                        int maxHp, int curHp,
                        int str, int dex,
                        int score, boolean[] keys,
                        String name, List<String> logMessages) {

    private static FogState[][] createEmptyFog(int height, int width) {
        FogState[][] fog = new FogState[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                fog[y][x] = FogState.HIDDEN;
            }
        }
        return fog;
    }

    public FogState[][] fog() {
        if (fog == null) {
            return createEmptyFog(field.length, field[0].length);
        }
        return fog;
    }
}