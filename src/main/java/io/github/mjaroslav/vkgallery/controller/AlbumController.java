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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
import java.util.stream.IntStream;

public class AlbumController extends AbstractGalleryController {

    protected PhotoAlbumFull album;
    protected final List<Photo> photos = new ArrayList<>();
    protected int prevPage = -1;
    protected int page = 0;

    @FXML
    public ChoiceBox<String> current;

    @FXML
    public Button prev;

    @FXML
    public Button next;

    @FXML
    public Label pages;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        current.setValue("1");
        current.valueProperty().addListener((observable, oldValue, newValue) -> {
            page = Integer.parseInt(newValue) - 1;
            initPage();
        });
    }

    @Override
    public void postInitialize() {
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                event.consume();
                try {
                    FXMLUtils.loadAndSetScene(VKGallery.PRIMARY_STAGE, "Main");
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
                //                new Thread(() -> {
                Platform.runLater(() -> {
                    pages.setText(curr + "/" + all);
                    current.getItems().add(curr + "");
                });
                //                }).start();
                System.out.println(curr + "/" + all);
                if (curr == 1) initPage();
                else updateButtons();
            });
        }).start();
    }

    public List<Photo> subList() {
        val result = new ArrayList<Photo>();
        val pageSize = VKGallery.CONFIG.getPageSize();
        System.out.println("begin");
        IntStream.range(page * pageSize, Math.min((page + 1) * pageSize, photos.size())).forEach(i -> {
            System.out.print(i + " ");
            result.add(photos.get(i));
        });
        System.out.println("end");
        return result;
    }


    public void initPage() {
        if (prevPage == page) return;
        if (prevPage == -1) prevPage = 0;
        new Thread(() -> {
            val list = subList().stream().map(item -> {
                Pair<Parent, PhotoController> pair = null;
                try {
                    pair = FXMLUtils.loadFXML("Photo");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pair.getRight().setImage(item);
                return pair;
            }).toList();
            list.forEach(pair -> photoControllers.put(pair.getLeft(), pair.getRight()));
            Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.getChildren().addAll(list.stream().map(Pair::getLeft).toList());
            });
        }).start();
        updateButtons();
    }

    public boolean haveNextPage() {
        return (1 + page) * VKGallery.CONFIG.getPageSize() < photos.size();
    }

    public void updateButtons() {
        Platform.runLater(() -> {
            prev.setDisable(page <= 0);
            next.setDisable(!haveNextPage());
        });
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
