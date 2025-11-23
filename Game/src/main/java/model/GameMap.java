package model;

public class GameMap {

    // 0 = suelo libre, 1 = muro/bloqueado, 2 = acantilado (solo escenario 2)
    public static final int TILE_FREE  = 0;
    public static final int TILE_WALL  = 1;
    public static final int TILE_CLIFF = 2;

    private final int[][] tiles;
    private final int tileSize;
    private final int rows;
    private final int cols;
    private final int width;
    private final int height;

    public GameMap(Scenario scenario) {
        this.tileSize = scenario.getTileSize();
        this.tiles = scenario.getTiles();

        if (tiles == null || tiles.length == 0 || tiles[0].length == 0) {
            throw new IllegalArgumentException("Matriz de tiles inválida para el escenario " + scenario);
        }

        this.rows = tiles.length;
        this.cols = tiles[0].length;
        this.width = cols * tileSize;
        this.height = rows * tileSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Devuelve el tile en coordenadas de columna/fila.
     * Fuera del mapa lo tratamos como pared.
     */
    public int getTile(int col, int row) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return TILE_WALL; // fuera del mundo = bloqueado
        }
        return tiles[row][col];
    }

    /**
     * Devuelve el tile en coordenadas del mundo (x,y).
     */
    public int getTileAt(double x, double y) {
        int col = (int) Math.floor(x / tileSize);
        int row = (int) Math.floor(y / tileSize);
        return getTile(col, row);
    }

    /**
     * Devuelve true si en esa celda hay un muro (1).
     */
    public boolean isBlocked(int col, int row) {
        return getTile(col, row) == TILE_WALL;
    }

    /**
     * Devuelve true si en esa posición del mundo hay acantilado (2).
     * Esto se usará solo por el escenario 2, porque solo allí hay 2 en el CSV.
     */
    public boolean isCliffAt(double x, double y) {
        return getTileAt(x, y) == TILE_CLIFF;
    }

    /**
     * Comprueba si un círculo de radio 'radius' puede moverse a (x, y)
     * sin chocar con muros (1). El acantilado (2) NO bloquea el movimiento.
     */
    public boolean canMoveTo(double x, double y, double radius) {
        int leftCol   = (int) ((x - radius) / tileSize);
        int rightCol  = (int) ((x + radius) / tileSize);
        int topRow    = (int) ((y - radius) / tileSize);
        int bottomRow = (int) ((y + radius) / tileSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (isBlocked(col, row)) {      // solo 1 bloquea
                    return false;
                }
            }
        }
        return true;
    }
}
