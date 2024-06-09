package io.github.mjaroslav.vkgallery.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class Controller implements Initializable {
    protected Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (root.getScene() != null && root.getScene().getWindow() instanceof Stage winStage) stage = winStage;
    }

    @FXML
    public AnchorPane root;

    public void postInitialize() {
    }
}
