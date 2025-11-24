package controller;

import javafx.fxml.FXML;
import app.MainApp;        // ajusta el paquete real

public class GameOverController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void onRetry() {
        // reinicia desde el primer escenario (cambia si quieres otro)
        mainApp.showGame(model.Scenario.PLAIN);
    }

    @FXML
    private void onMainMenu() {
        mainApp.showMainMenu();
    }
}
