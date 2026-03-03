package model.domain.geometry;

public class Corridor {

    private final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    private final int xStart, yStart, xEnd, yEnd, first, second, direction;

    public Corridor(int xStart, int yStart, int xEnd, int yEnd, int first, int second) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.first = first;
        this.second = second;
        switch (first - second) {
            case 3 -> direction = UP;
            case -1 -> direction = RIGHT;
            case -3 -> direction = DOWN;
            case 1 -> direction = LEFT;
            default -> direction = -1;
        }
    }

    int getxStart() { return xStart; }
    int getyStart() { return yStart; }
    int getxEnd() { return xEnd; }
    int getyEnd() { return yEnd; }

    public int getFirst() { return first; }
    public int getSecond() { return second; }

    public int[][] generatePathPoint() {
        int points = 4;
        int weidth = 0, heigth = 0;

        if (xStart != xEnd) weidth = Math.abs(xStart - xEnd);
        if (yStart != yEnd) heigth = Math.abs(yStart - yEnd);

        if (weidth == 0 || heigth == 0) points -= 2;

        int[][] path = new int[points][2];
        path[0][0] = xStart;
        path[0][1] = yStart;
        if (points == 2) {
            switch (direction) {
                case UP, DOWN -> {
                    path[1][0] = xStart;
                    path[1][1] = yEnd;
                }
                case RIGHT, LEFT -> {
                    path[1][0] = xEnd;
                    path[1][1] = yStart;
                }
                default -> {}
            }
        } else {
            path[points - 1][0] = xEnd;
            path[points - 1][1] = yEnd;
            int current = 1;
            heigth = (heigth / 2 == 0) ? 2 : heigth / 2;
            weidth = (weidth / 2 == 0) ? 2 : weidth / 2;
            switch (direction) {
                case UP -> {
                    path[current][0] = xStart;
                    path[current++][1] = yStart - heigth;
                    path[current][0] = xEnd;
                    path[current][1] = yStart - heigth;
                }
                case RIGHT -> {
                    path[current][0] = xStart + weidth;
                    path[current++][1] = yStart;
                    path[current][0] = xStart + weidth;
                    path[current][1] = yEnd;
                }
                case DOWN -> {
                    path[current][0] = xStart;
                    path[current++][1] = yStart + heigth;
                    path[current][0] = xEnd;
                    path[current][1] = yStart + heigth;
                }
                case LEFT -> {
                    path[current][0] = xStart - weidth;
                    path[current++][1] = yStart;
                    path[current][0] = xStart - weidth;
                    path[current][1] = yEnd;
                }
            }
        }

        return path;
    }

}