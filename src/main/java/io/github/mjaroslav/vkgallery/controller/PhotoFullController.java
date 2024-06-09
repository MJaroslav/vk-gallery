package io.github.mjaroslav.vkgallery.controller;

import com.vk.api.sdk.objects.photos.Photo;
import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import io.github.mjaroslav.vkgallery.util.ResourceManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lombok.Setter;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PhotoFullController extends Controller {
    protected Photo photo;
    @Setter
    protected Scene lastScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        image.setCache(false);
    }

    @Override
    public void postInitialize() {
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                event.consume();
                try {
                    if (lastScene != null) VKGallery.PRIMARY_STAGE.setScene(lastScene);
                    else FXMLUtils.changeScene(VKGallery.PRIMARY_STAGE, "Main");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    public ImageView image;

    public void setImage(@NotNull Photo photo) {
        this.photo = photo;
        val last = photo.getSizes().getLast();
        if (last == null) return;
        val cached = ResourceManager.getCacheDirectory()
            .resolve(photo.getOwnerId() + "_" + photo.getId() + "_" + last.getType() + ".jpg");
        if (!FileUtils.isRegularFile(cached.toFile())) {
            try {
                FileUtils.createParentDirectories(cached.toFile());
                FileUtils.copyURLToFile(last.getUrl().toURL(), cached.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Image image =
            new Image(cached.toFile().toURI().toString(), last.getWidth(), last.getHeight(), true, true, true);
        Platform.runLater(() -> {
            this.image.setImage(image);
        });
    }
}
