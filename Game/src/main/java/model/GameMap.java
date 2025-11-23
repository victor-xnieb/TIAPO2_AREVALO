package model;

public class GameMap {

    private final int[][] tiles; // 0 = libre, 1 = bloqueado
    private final int tileSize;

    public GameMap(int[][] tiles, int tileSize) {
        this.tiles = tiles;
        this.tileSize = tileSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getCols() {
        return tiles[0].length;
    }

    public int getRows() {
        return tiles.length;
    }

    public double getWidth() {
        return getCols() * tileSize;
    }

    public double getHeight() {
        return getRows() * tileSize;
    }

    public int getTile(int col, int row) {
        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) {
            return 1;
        }
        return tiles[row][col];
    }

    public boolean isBlocked(int col, int row) {
        return getTile(col, row) == 1;
    }

    public boolean canMoveTo(double x, double y, double radius) {
        int leftCol   = (int) ((x - radius) / tileSize);
        int rightCol  = (int) ((x + radius) / tileSize);
        int topRow    = (int) ((y - radius) / tileSize);
        int bottomRow = (int) ((y + radius) / tileSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (isBlocked(col, row)) {
                    return false;
                }
            }
        }
        return true;
    }
}
