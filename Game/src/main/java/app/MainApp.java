package app;

import controller.AchievementsController;
import controller.GameController;
import controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Scenario;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

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

            Scene scene = new Scene(root); // ✅ sin ancho/alto fijos
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
            controller.initGame();

            Scene scene = new Scene(root); // ✅

            scene.setOnKeyPressed(e -> controller.onKeyPressed(e.getCode()));
            scene.setOnKeyReleased(e -> controller.onKeyReleased(e.getCode()));

            // si ocultas el cursor:
            scene.setCursor(javafx.scene.Cursor.NONE);

            primaryStage.setTitle("Oregon Trail Survival - " + scenario.getDisplayName());
            primaryStage.setScene(scene);
            // no hace falta show() otra vez, el Stage ya está visible y maximizado
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void showAchievements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/achievements_view.fxml"));
            Parent root = loader.load();
            AchievementsController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root); // ✅
            primaryStage.setTitle("Oregon Trail Survival - Logros");
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
