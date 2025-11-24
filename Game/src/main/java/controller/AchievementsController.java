package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.AchievementsManager;
import app.MainApp;

import java.util.List;

public class AchievementsController {

    @FXML
    private ListView<String> achievementsList;

    private Stage stage;

    private AchievementsManager achievementsManager;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // NUEVO: lo llama MainApp al crear la ventana
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAchievementsManager(AchievementsManager manager) {
        this.achievementsManager = manager;
        refreshList();
    }

    private void refreshList() {
        if (achievementsManager == null) return;

        List<String> lines = achievementsManager.getAllAchievementLines();
        achievementsList.getItems().setAll(lines);
    }

    @FXML
    private void onBackToMenu() {
        if (stage != null) {
            stage.close();
        }
    }

}
