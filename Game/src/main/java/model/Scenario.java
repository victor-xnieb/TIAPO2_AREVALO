package model;

public enum Scenario {

    PLAIN(
            "Escenario 1 - Llanuras",
            "/images/scenario3.png",
            20,
            null,
            "/maps/MapaLlanurasCSV.csv",
            90,
            11,      // CM12
            10,      // maxEnemies
            plainFoodTiles(),
            plainHealTiles()
    ),
    MOUNTAIN(
            "Escenario 2 - Montañas",
            "/images/scenario2.png",
            20,
            null,
            "/maps/MapaAcantiladoCSV.csv",
            88,
            44,
            20,
            mountainFoodTiles(),
            mountainHealTiles()
    ),
    RIVER(
            "Escenario 3 - Río",
            "/images/scenario1.png",
            20,
            null,
            "/maps/MapaRioCSV.csv",
            21,
            12,
            25,
            riverFoodTiles(),
            riverHealTiles()
    );

    private final String displayName;
    private final String imagePath;
    private final int tileSize;
    private int[][] tiles;
    private final String mapPath;

    private final Integer rifleCol;
    private final Integer rifleRow;

    private final int maxEnemies;

    // 🔥 NUEVO: tiles con comida y con curación
    private final int[][] foodTiles;
    private final int[][] healTiles;

    Scenario(String displayName,
             String imagePath,
             int tileSize,
             int[][] tiles,
             String mapPath,
             Integer rifleCol,
             Integer rifleRow,
             int maxEnemies,
             int[][] foodTiles,
             int[][] healTiles) {
        this.displayName = displayName;
        this.imagePath = imagePath;
        this.tileSize = tileSize;
        this.tiles = tiles;
        this.mapPath = mapPath;
        this.rifleCol = rifleCol;
        this.rifleRow = rifleRow;
        this.maxEnemies = maxEnemies;
        this.foodTiles = foodTiles;
        this.healTiles = healTiles;
    }

    public String getDisplayName() { return displayName; }
    public String getImagePath() { return imagePath; }
    public int getTileSize() { return tileSize; }

    public int[][] getTiles() {
        if (tiles != null) return tiles;
        if (mapPath != null) {
            tiles = MapLoader.loadTiles(mapPath);
            return tiles;
        }
        throw new IllegalStateException("Scenario sin tiles ni CSV: " + name());
    }

    public Integer getRifleCol() { return rifleCol; }
    public Integer getRifleRow() { return rifleRow; }
    public int getMaxEnemies()   { return maxEnemies; }

    // NUEVO
    public int[][] getFoodTiles() { return foodTiles; }
    public int[][] getHealTiles() { return healTiles; }

    // ========= aquí eliges tú los tiles (0-based) =========
    // ahora te pongo ejemplos; luego cambias por tus coords (tipo col, fila)

    private static int[][] plainFoodTiles() {
        // Escenario 1: 5 comidas
        return new int[][]{
                {24, 30},
                {98, 14},
                {130, 53},
                {84, 87},
                {33, 79}
        };
    }

    private static int[][] plainHealTiles() {
        // Escenario 1: 4 curaciones
        return new int[][]{
                {9, 5},
                {156, 11},
                {114, 98},
                {10, 63}
        };
    }

    private static int[][] mountainFoodTiles() {
        // Escenario 2: 4 comidas
        return new int[][]{
                {126, 51},
                {36, 42},
                {83, 59},
                {31, 82}
        };
    }

    private static int[][] mountainHealTiles() {
        // Escenario 2: 3 curaciones
        return new int[][]{
                {77, 25},
                {39, 18},
                {75, 94}
        };
    }

    private static int[][] riverFoodTiles() {
        // Escenario 3: 3 comidas
        return new int[][]{
                {8, 43},
                {89, 12},
                {99, 48}
        };
    }

    private static int[][] riverHealTiles() {
        // Escenario 3: 2 curaciones
        return new int[][]{
                {111, 26},
                {19, 9}
        };
    }
}
