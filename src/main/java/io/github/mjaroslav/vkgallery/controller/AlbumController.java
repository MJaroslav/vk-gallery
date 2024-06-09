package io.github.mjaroslav.vkgallery.controller;

import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AlbumController extends AbstractGalleryController {
    protected PhotoAlbumFull album;
    protected final List<Photo> photos = new ArrayList<>();
    protected int prevPage = -1;
    protected int page = 0;
    protected final int pageSize = 100;

    @FXML
    public Button prev;

    @FXML
    public Button next;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @Override
    public void postInitialize() {
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                event.consume();
                try {
                    FXMLUtils.changeScene(VKGallery.PRIMARY_STAGE, "Main");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void initNodes(@NotNull TilePane pane) {
    }


    public void setAlbum(@NotNull PhotoAlbumFull album) {
        this.album = album;
        photos.clear();
        new Thread(() -> {
            VKGallery.VK.getAllPhotos(album, (curr, all, list) -> {
                photos.addAll(list);
                System.out.println(curr + "/" + all);
                initPage();
            });
        }).start();
    }

    public List<Photo> subList() {
        val result = new ArrayList<Photo>();
        return photos.subList(page * pageSize, Math.min((page + 1) * pageSize, photos.size() - 1));
    }


    public void initPage() {
        if (prevPage == page) return;
        if (prevPage == -1) prevPage = 0;
        new Thread(() -> {
            Platform.runLater(() -> {
                pane.getChildren().clear();
            });
            val list = subList().stream().map(item -> {
                Pair<Parent, PhotoController> pair = null;
                try {
                    pair = FXMLUtils.loadFXML("Photo");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pair.getRight().setImage(item);
                photoControllers.put(pair.getLeft(), pair.getRight());
                return pair.getLeft();
            }).toList();
            Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.getChildren().addAll(list);
            });
        }).start();
        Platform.runLater(() -> {
            prev.setDisable(page == 0);
            next.setDisable(!haveNextPage());
        });
    }

    public boolean haveNextPage() {
        return (1 + page) * pageSize < photos.size();
    }

    public void prevClicked(ActionEvent actionEvent) {
        prevPage = page;
        page--;
        initPage();
    }

    public void nextClicked(ActionEvent actionEvent) {
        prevPage = page;
        page++;
        initPage();
    }
}
