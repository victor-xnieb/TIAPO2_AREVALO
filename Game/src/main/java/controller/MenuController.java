package controller;

import app.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.Scenario;

public class MenuController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleScenario1(ActionEvent event) {
        mainApp.showGame(Scenario.PLAIN);
    }

    @FXML
    private void handleScenario2(ActionEvent event) {
        mainApp.showGame(Scenario.MOUNTAIN);
    }

    @FXML
    private void handleScenario3(ActionEvent event) {
        mainApp.showGame(Scenario.RIVER);
    }

    @FXML
    private void handleAchievements(ActionEvent event) {
        mainApp.showAchievementsView();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}
