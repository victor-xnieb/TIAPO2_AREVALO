package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import app.MainApp;      // ajusta paquete

public class VictoryController {

    private MainApp mainApp;

    @FXML
    private Label scoreLabel;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /** Llamado desde MainApp después de cargar el FXML. */
    public void setScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Puntaje total de logros: " + score);
        }
    }

    @FXML
    private void onPlayAgain() {
        mainApp.showGame(model.Scenario.PLAIN); // vuelve a empezar desde el 1
    }

    @FXML
    private void onMainMenu() {
        mainApp.showMainMenu();
    }
}
