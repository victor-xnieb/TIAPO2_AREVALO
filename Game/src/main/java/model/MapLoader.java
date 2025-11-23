package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga matrices de tiles (int[][]) desde archivos CSV en resources.
 * Pensado para mapas con valores 0/1 separados por punto y coma (;).
 */
public final class MapLoader {

    private MapLoader() {
        // clase utilitaria, no instanciable
    }

    /**
     * Carga un mapa desde un recurso en el classpath.
     *
     * @param resourcePath ruta dentro de resources, por ejemplo "/maps/MapaRioCSV.csv"
     * @return matriz de tiles int[filas][columnas]
     */
    public static int[][] loadTiles(String resourcePath) {
        try (InputStream is = MapLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("No se encontró el recurso: " + resourcePath);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<int[]> rows = new ArrayList<>();
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        // línea vacía al final del archivo, la ignoramos
                        continue;
                    }

                    // separar por ; (Excel en español) y quitar espacios
                    String[] raw = line.split(";");
                    List<Integer> values = new ArrayList<>();

                    for (String part : raw) {
                        part = part.trim();
                        if (part.isEmpty()) {
                            // celda vacía -> la ignoramos (si quieres que cuente como 0, aquí se podría meter un 0)
                            continue;
                        }
                        values.add(Integer.parseInt(part));
                    }

                    if (values.isEmpty()) {
                        // línea sin números reales, la saltamos
                        continue;
                    }

                    int[] row = new int[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        row[i] = values.get(i);
                    }

                    rows.add(row);
                }

                if (rows.isEmpty()) {
                    throw new IllegalArgumentException("Archivo de mapa vacío: " + resourcePath);
                }

                int cols = rows.get(0).length;
                int[][] tiles = new int[rows.size()][cols];

                for (int r = 0; r < rows.size(); r++) {
                    if (rows.get(r).length != cols) {
                        throw new IllegalArgumentException(
                                "Número de columnas inconsistente en " + resourcePath +
                                        " en la fila " + r
                        );
                    }
                    tiles[r] = rows.get(r);
                }

                return tiles;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo mapa: " + resourcePath, e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Mapa con valores no numéricos en: " + resourcePath, e);
        }
    }
}
