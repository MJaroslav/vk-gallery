package io.github.mjaroslav.vkgallery.controller;

import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import io.github.mjaroslav.vkgallery.util.ResourceManager;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PhotoController extends Controller implements Initializable {
    protected Photo photo;
    protected PhotoAlbumFull album;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        image.setCache(false);
        val min = label.getHeight();
        label.hoverProperty().addListener((observable, oldValue, newValue) -> {
            val animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(250));
                }

                @Override
                protected void interpolate(double frac) {
                    val curr = (75 - min) * (newValue ? frac : 1 - frac);
                    label.setPrefHeight(curr);
                }
            };
            animation.play();
        });
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            resizeImage(newValue.doubleValue());
        });
    }

    @FXML
    public ImageView image;

    @FXML
    public Label label;

    @FXML
    public AnchorPane root;

    public void load() {
        val img = loadImage();
        if (img != null) image.setImage(img);
    }

    public void unload() {
        image.setImage(null);
    }

    protected void resizeImage(double size) {
        val img = image.getImage();
        if (img == null) return;
        image.setFitWidth(size);
        image.setFitHeight(size);
        if (img.getWidth() > img.getHeight()) {
            image.setViewport(
                new Rectangle2D((img.getWidth() - img.getHeight()) / 2, 0, img.getHeight(), img.getHeight()));
        }
        else {
            image.setViewport(
                new Rectangle2D(0, (img.getHeight() - img.getWidth()) / 2, img.getWidth(), img.getWidth()));
        }
        label.setPrefWidth(size);
    }

    protected @Nullable Image loadImage() {
        val prevSize = VKGallery.CONFIG.getPreviewSize();
        if (photo != null) {
            val last = getSizeForPreview(photo.getSizes());
            if (last == null) return null;
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
            Image image;
            if (last.getWidth() > last.getHeight()) {
                image = new Image(cached.toFile().toURI().toString(), 0, prevSize, true, true, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1) resizeImage(root.getWidth());
                });
//                image.progressProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue.doubleValue() == 1) {
//                        this.image.setViewport(
//                            new Rectangle2D((image.getWidth() - image.getHeight()) / 2, 0, image.getHeight(),
//                                image.getHeight()));
//                    }
//                });
            }
            else {
                image = new Image(cached.toFile().toURI().toString(), prevSize, 0, true, true, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1) resizeImage(root.getWidth());
                });
//                image.progressProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue.doubleValue() == 1) {
//                        this.image.setViewport(
//                            new Rectangle2D(0, (image.getHeight() - image.getWidth()) / 2, image.getWidth(),
//                                image.getWidth()));
//                    }
//                });
            }
            Platform.runLater(() -> {
                this.image.setImage(image);
                label.setText(photo.getOwnerId() + "_" + photo.getId());
            });
            return image;
        }
        else {
            val last = getSizeForPreview(album.getSizes());
            if (last == null) return null;
            val cached = ResourceManager.getCacheDirectory()
                .resolve("album_" + album.getOwnerId() + "_" + album.getId() + "_" + last.getType() + ".jpg");
            if (!FileUtils.isRegularFile(cached.toFile())) {
                try {
                    FileUtils.createParentDirectories(cached.toFile());
                    FileUtils.copyURLToFile(last.getUrl().toURL(), cached.toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Image image;
            if (last.getWidth() > last.getHeight()) {
                image = new Image(cached.toFile().toURI().toString(), 0, prevSize, true, true, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1) resizeImage(root.getWidth());
                });
//                image.progressProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue.doubleValue() == 1) {
//                        this.image.setViewport(
//                            new Rectangle2D((image.getWidth() - image.getHeight()) / 2, 0, image.getHeight(),
//                                image.getHeight()));
//                    }
//                });
            }
            else {
                image = new Image(cached.toFile().toURI().toString(), prevSize, 0, true, true, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1) resizeImage(root.getWidth());
                });
//                image.progressProperty().addListener((observable, oldValue, newValue) -> {
//                    if (newValue.doubleValue() == 1) {
//                        this.image.setViewport(
//                            new Rectangle2D(0, (image.getHeight() - image.getWidth()) / 2, image.getWidth(),
//                                image.getWidth()));
//                    }
//                });
            }
            Platform.runLater(() -> {
                this.image.setImage(image);
                label.setText(album.getTitle());
            });
            return image;
        }
    }

    public void setImage(@NotNull Photo photo) {
        this.photo = photo;
    }

    public void setAlbum(@NotNull PhotoAlbumFull album) {
        this.album = album;
    }

    private PhotoSizes getSizeForPreview(@NotNull List<PhotoSizes> sizes) {
        val prevSize = VKGallery.CONFIG.getPreviewSize();
        return sizes.stream().filter(size -> size.getHeight() >= prevSize || size.getWidth() >= prevSize).findFirst()
            .orElseThrow();
    }

    @SneakyThrows
    public void onClicked(@NotNull MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (album != null) {
                Pair<Parent, AlbumController> pair = FXMLUtils.loadAndSetScene(VKGallery.PRIMARY_STAGE, "Album");
                pair.getRight().setAlbum(album);
            }
            else {
                Pair<Parent, PhotoFullController> pair =
                    FXMLUtils.loadAndSetScene(VKGallery.PRIMARY_STAGE, "PhotoFull");
                pair.getRight().setImage(photo);
                pair.getRight().setLastScene(root.getScene());
            }
        }
    }
}
