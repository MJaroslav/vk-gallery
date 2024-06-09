package io.github.mjaroslav.vkgallery.controller;

import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.TilePane;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractGalleryController {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @Override
    public void initNodes(@NotNull TilePane pane) {
        new Thread(() -> {
            for (var item : VKGallery.VK.getAlbums().getItems()) {
                try {
                    Pair<Parent, PhotoController> pair = FXMLUtils.loadFXML("Photo");
                    pair.getRight().setAlbum(item);
                    photoControllers.put(pair.getLeft(), pair.getRight());
                    Platform.runLater(() -> pane.getChildren().add(pair.getLeft()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
