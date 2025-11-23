package controller;

import app.MainApp;
import datastructures.AchievementTree;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class AchievementsController {

    private MainApp mainApp;

    @FXML
    private TextArea achievementArea;

    private AchievementTree achievementTree;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        initAchievements();
    }

    private void initAchievements() {
        achievementTree = new AchievementTree();
        achievementTree.insert(new AchievementTree.Achievement(
                "Primer disparo", "Realizaste tu primer disparo.", 10));
        achievementTree.insert(new AchievementTree.Achievement(
                "Primera victoria", "Completaste un escenario.", 30));
        achievementTree.insert(new AchievementTree.Achievement(
                "Superviviente", "Sobreviviste mucho tiempo.", 50));

        achievementArea.setText(achievementTree.inOrderString());
    }

    @FXML
    private void handleBack() {
        mainApp.showMainMenu();
    }
}
