package app;

import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AchievementsManager;
import model.Scenario;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

    private final AchievementsManager achievementsManager = new AchievementsManager();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // si quieres "ventana maximizada"
        primaryStage.setMaximized(true);

        // si quieres FULLSCREEN de verdad (sin barra de tareas, como un juego)
        // primaryStage.setFullScreen(true);
        // primaryStage.setFullScreenExitHint(""); // quita el mensaje de "presione ESC"

        showMainMenu();
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root); // sin ancho/alto fijos
            primaryStage.setTitle("Oregon Trail Survival - Menú");
            primaryStage.setScene(scene);

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGame(Scenario scenario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_view.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setMainApp(this);
            controller.setScenario(scenario);
            controller.setAchievementsManager(achievementsManager);
            controller.initGame();

            Scene scene = new Scene(root); // ✅

            scene.setOnKeyPressed(e -> controller.onKeyPressed(e.getCode()));
            scene.setOnKeyReleased(e -> controller.onKeyReleased(e.getCode()));

            // si ocultas el cursor:
            scene.setCursor(javafx.scene.Cursor.NONE);

            gameScene = scene;
            currentGameController = controller;

            primaryStage.setTitle("Oregon Trail Survival - " + scenario.getDisplayName());
            primaryStage.setScene(scene);
            // no hace falta show() otra vez, el Stage ya está visible y maximizado
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // en MainApp
    public void showAchievementsView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/achievements_view.fxml"));
            Parent root = loader.load();

            // controlador
            AchievementsController controller = loader.getController();
            controller.setAchievementsManager(achievementsManager);

            // creamos una ventana (Stage) independiente
            Stage achievementsStage = new Stage();
            achievementsStage.setTitle("Árbol de logros");
            achievementsStage.initOwner(primaryStage);          // dueño = la ventana principal
            // Si quieres que bloquee la interacción con el juego:
            // achievementsStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            achievementsStage.setScene(scene);

            // le damos al controller una referencia al Stage para poder cerrarlo
            controller.setStage(achievementsStage);

            achievementsStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void showGameOver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/game_over.fxml")
            );
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Oregon Trail Survival - Game Over");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showVictory() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/victory_view.fxml")
            );
            Parent root = loader.load();

            VictoryController controller = loader.getController();
            controller.setMainApp(this);

            int score = achievementsManager.getTotalScore();
            controller.setScore(score);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Oregon Trail Survival - Victoria");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showManualFromMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manual_view.fxml"));
            Parent root = loader.load();

            ManualController controller = loader.getController();
            controller.setMainApp(this);
            controller.setOpenedFromGame(false); // viene del menú

            Scene scene = new Scene(root);
            primaryStage.setTitle("Oregon Trail Survival - Manual");
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Scene gameScene;
    private GameController currentGameController;
    private Scene previousScene;  // escena a la que volvemos


    public void showManualFromGame() {
        previousScene = gameScene;           // guarda la escena del juego
        currentGameController.pauseGame();   // método que pare el AnimationTimer

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manual_view.fxml"));
            Parent root = loader.load();

            ManualController controller = loader.getController();
            controller.setMainApp(this);
            controller.setOpenedFromGame(true);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnFromManualToGame() {
        primaryStage.setScene(gameScene);
        if (currentGameController != null) {
            currentGameController.resumeGame(); // vuelve a arrancar el AnimationTimer
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}

