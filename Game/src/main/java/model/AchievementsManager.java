package model;

import datastructures.AchievementTree;

import java.util.*;

public class AchievementsManager {

    private final AchievementTree tree;
    private final Set<String> unlockedNames;
    private final Map<String, AchievementTree.Achievement> achievements = new HashMap<>();

    public AchievementsManager() {
        this.tree = new AchievementTree();
        this.unlockedNames = new HashSet<>();
        registerDefaultAchievements();
    }

    private void registerDefaultAchievements() {
        // Aquí metes TODOS los logros que listamos antes.
        // Puedes ajustar puntos como quieras: más puntos = más “importante”.

        tree.insert(new AchievementTree.Achievement(
                "WELCOME",
                "Bienvenido al Oeste",
                5
        ));
        tree.insert(new AchievementTree.Achievement(
                "FIRST_BLOOD",
                "Primer disparo certero (derrota a tu primer enemigo)",
                10
        ));
        tree.insert(new AchievementTree.Achievement(
                "HUNTER_NOVICE",
                "Cazador novato (elimina 5 enemigos)",
                15
        ));
        tree.insert(new AchievementTree.Achievement(
                "HUNTER_EXPERT",
                "Cazador experto (elimina 15 enemigos)",
                20
        ));
        tree.insert(new AchievementTree.Achievement(
                "HUNTER_LEGEND",
                "Leyenda del Oeste (elimina 30 enemigos)",
                30
        ));
        tree.insert(new AchievementTree.Achievement(
                "RIFLE_MASTER",
                "Maestro del rifle (elimina 10 enemigos con el rifle)",
                25
        ));
        tree.insert(new AchievementTree.Achievement(
                "REVOLVER_ONLY",
                "Pistola confiable (completa un escenario usando solo el revólver)",
                18
        ));

        // Progreso de niveles
        tree.insert(new AchievementTree.Achievement(
                "LEVEL1_CLEAR",
                "Superviviente de las llanuras",
                10
        ));
        tree.insert(new AchievementTree.Achievement(
                "LEVEL2_CLEAR",
                "Conquistador de las montañas",
                20
        ));
        tree.insert(new AchievementTree.Achievement(
                "LEVEL3_CLEAR",
                "Señor del río",
                30
        ));
        tree.insert(new AchievementTree.Achievement(
                "GAME_FINISHED",
                "Leyenda del Oregon Trail",
                40
        ));

        // Llaves y portal
        tree.insert(new AchievementTree.Achievement(
                "KEY_COLLECTOR_I",
                "Recolector de llaves I (4 llaves del escenario 1)",
                12
        ));
        tree.insert(new AchievementTree.Achievement(
                "KEY_COLLECTOR_II",
                "Recolector de llaves II (4 llaves del escenario 2)",
                22
        ));
        tree.insert(new AchievementTree.Achievement(
                "KEY_COLLECTOR_III",
                "Maestro de las llaves (4 llaves del escenario 3)",
                32
        ));
        tree.insert(new AchievementTree.Achievement(
                "PORTAL_TRAVELER",
                "Viajero de portales (usa un portal para avanzar)",
                18
        ));

        // Vida / comida / medicinas
        tree.insert(new AchievementTree.Achievement(
                "HEALER_APPRENTICE",
                "Aprendiz de curandero (usa 3 medicinas)",
                8
        ));
        tree.insert(new AchievementTree.Achievement(
                "HEALER_MASTER",
                "Curandero del campamento (usa 8 medicinas)",
                28
        ));
        tree.insert(new AchievementTree.Achievement(
                "FOOD_LOVER",
                "Hambriento pero feliz (come 5 veces)",
                8
        ));

        tree.insert(new AchievementTree.Achievement(
                "NO_DAMAGE_LEVEL",
                "Intocable (completa un escenario sin recibir daño)",
                35
        ));

        // Munición
        tree.insert(new AchievementTree.Achievement(
                "FIRST_RELOAD",
                "Recarga táctica (recarga por primera vez)",
                6
        ));
        tree.insert(new AchievementTree.Achievement(
                "AMMO_SAVER",
                "Ahorrador de balas (termina un escenario con 30 balas o más)",
                24
        ));
        tree.insert(new AchievementTree.Achievement(
                "RELOAD_FAN",
                "Recargador compulsivo (realiza 15 recargas)",
                22
        ));

        // Estilo raro
        tree.insert(new AchievementTree.Achievement(
                "PACIFIST",
                "Vaquero pacifista (termina un escenario sin disparar)",
                27
        ));
        tree.insert(new AchievementTree.Achievement(
                "LAST_CHANCE",
                "Al borde del abismo (sobrevive habiendo tenido 1 solo corazón)",
                16
        ));
    }

    // -------- API que usarán GameController y el AchievementsController --------

    public void unlock(String name) {
        unlockedNames.add(name);
        registerAchievement(tree.findByName(name));
    }

    public boolean isUnlocked(String name) {
        return unlockedNames.contains(name);
    }

    /**
     * Devuelve todas las líneas (ordenadas por puntos) para mostrar en la UI.
     * Prefijo:
     *  [✔] si está desbloqueado
     *  [ ]  si todavía no
     */
    public List<String> getAllAchievementLines() {
        List<String> lines = new ArrayList<>();

        tree.forEachInOrder(achievement -> {
            String id = achievement.getName(); // usamos name como ID
            boolean unlocked = unlockedNames.contains(id);
            String prefix = unlocked ? "[✔] " : "[ ] ";
            String line = prefix
                    + achievement.getName()
                    + " - "
                    + achievement.getDescription()
                    + " (" + achievement.getPoints() + " pts)";
            lines.add(line);
        });

        return lines;
    }



    public int getTotalScore() {
        int total = 0;
        for (String name : unlockedNames) {
            AchievementTree.Achievement a = achievements.get(name);
            if (a != null) {
                total += a.getPoints();
            }
        }
        return total;
    }


    public void registerAchievement(AchievementTree.Achievement a) {
        achievements.put(a.getName(), a);
        // y lo metes a tu AchievementTree interno si quieres
    }

}
