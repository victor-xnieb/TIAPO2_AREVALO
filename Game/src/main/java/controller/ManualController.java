package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import app.MainApp;
import model.ManualTexts;

public class ManualController {

    @FXML
    private TextArea manualArea;

    private MainApp mainApp;
    private boolean openedFromGame = false;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setOpenedFromGame(boolean openedFromGame) {
        this.openedFromGame = openedFromGame;
    }

    @FXML
    private void initialize() {
        manualArea.setEditable(false);
        manualArea.setWrapText(true);
        manualArea.setText(ManualTexts.FULL_MANUAL); // clase util donde pones el texto de arriba
    }

    @FXML
    private void onBackClicked() {
        if (openedFromGame) {
            mainApp.returnFromManualToGame();
        } else {
            mainApp.showMainMenu();
        }
    }
}
