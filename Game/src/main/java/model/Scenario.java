package model;

public enum Scenario {

    PLAIN(
            "Escenario 1 - Llanuras",
            "/images/scenario3.png",
            20,
            null,
            "/maps/MapaLlanurasCSV.csv",
            90, 11      // 👉 CM12 (col 90, fila 11 en 0-based)
    ),
    MOUNTAIN(
            "Escenario 2 - Montañas",
            "/images/scenario2.png",
            20,
            null,
            "/maps/MapaAcantiladoCSV.csv",
            88, 44  // 👉 en este mapa no hay rifle en el suelo
    ),
    RIVER(
            "Escenario 3 - Río",
            "/images/scenario1.png",
            20,
            null,
            "/maps/MapaRioCSV.csv",
            21, 12  // 👉 tampoco rifle tirado aquí (por ahora)
    );

    private final String displayName;
    private final String imagePath;
    private final int tileSize;

    // opción A: matriz ya construida en código
    private int[][] tiles;

    // opción B: ruta a CSV para cargar las tiles
    private final String mapPath;

    private final Integer rifleCol;
    private final Integer rifleRow;

    Scenario(String displayName,
             String imagePath,
             int tileSize,
             int[][] tiles,
             String mapPath,
             Integer rifleCol,
             Integer rifleRow) {
        this.displayName = displayName;
        this.imagePath = imagePath;
        this.tileSize = tileSize;
        this.tiles = tiles;
        this.mapPath = mapPath;
        this.rifleCol = rifleCol;
        this.rifleRow = rifleRow;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int[][] getTiles() {
        // si ya hay matriz cargada, la devolvemos
        if (tiles != null) {
            return tiles;
        }
        // si no hay matriz pero sí hay CSV, lo leemos una vez
        if (mapPath != null) {
            tiles = MapLoader.loadTiles(mapPath);
            return tiles;
        }
        throw new IllegalStateException("Scenario sin tiles ni CSV: " + name());
    }

    public Integer getRifleCol() {
        return rifleCol;
    }

    public Integer getRifleRow() {
        return rifleRow;
    }
}
