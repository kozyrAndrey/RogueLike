package model.domain.geometry;

public class GridGraph {
    private final int size;
    private int[][] adjMatrix;

    public GridGraph(int rows, int cols) {
        this.size = rows * cols;
        this.adjMatrix = new int[size][size];
        buildGridGraph(rows, cols);
    }

    public int[][] getAdjMatrix() { return adjMatrix; }

    public void buildGridGraph(int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int currentVer = i * cols + j;

                if (j > 0) {
                    int leftNeighbor = i * cols + (j - 1);
                    adjMatrix[currentVer][leftNeighbor] = 1;
                    adjMatrix[leftNeighbor][currentVer] = 1;
                }
                if (j < cols - 1) {
                    int rightNeighbor = i * cols + (j + 1);
                    adjMatrix[currentVer][rightNeighbor] = 1;
                    adjMatrix[rightNeighbor][currentVer] = 1;
                }
                if (i > 0) {
                    int topNeighbor = (i - 1) * cols + j;
                    adjMatrix[currentVer][topNeighbor] = 1;
                    adjMatrix[topNeighbor][currentVer] = 1;
                }
                if (i < rows - 1) {
                    int downNeighbor = (i + 1) * cols + j;
                    adjMatrix[currentVer][downNeighbor] = 1;
                    adjMatrix[downNeighbor][currentVer] = 1;
                }
            }
        }
    }
}
