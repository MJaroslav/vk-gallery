package io.github.mjaroslav.vkgallery.controller;

import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;

public abstract class AbstractGalleryController extends Controller {
    protected final List<Node> preDisplayNodes = new ArrayList<>();
    protected final Map<Node, PhotoController> photoControllers = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
//        root.widthProperty()
//            .addListener((observable, oldValue, newValue) -> pane.setPrefColumns(newValue.intValue() / 200));
        pane.setPrefColumns(VKGallery.CONFIG.getColumns());
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            pane.setPrefTileWidth(newValue.intValue() / (double)VKGallery.CONFIG.getColumns());
            pane.setPrefTileHeight(newValue.intValue() / (double)VKGallery.CONFIG.getColumns());
        });
        scroll.widthProperty().addListener((observable, oldValue, newValue) -> updateVisibleElements());
        scroll.heightProperty().addListener((observable, oldValue, newValue) -> updateVisibleElements());
        scroll.vvalueProperty().addListener((observable, oldValue, newValue) -> updateVisibleElements());
        scroll.hvalueProperty().addListener((observable, oldValue, newValue) -> updateVisibleElements());
        pane.getChildren().addListener((ListChangeListener<? super Node>) c -> updateVisibleElements());
        initNodes(pane);
    }

    public void updateVisibleElements() {
        val current = FXMLUtils.getVisibleNodes(scroll);
        preDisplayNodes.stream().filter(n -> !current.contains(n)).forEach(this::onNodeOutFromDisplay);
        current.stream().filter(n -> !preDisplayNodes.contains(n)).forEach(this::onNodeInToDisplay);
        preDisplayNodes.clear();
        preDisplayNodes.addAll(current);
    }

    public abstract void initNodes(@NotNull TilePane pane);

    public void onNodeOutFromDisplay(@NotNull Node node) {
        photoControllers.get(node).unload();
    }

    public void onNodeInToDisplay(@NotNull Node node) {
        photoControllers.get(node).load();
    }

    @FXML
    public TilePane pane;

    @FXML
    public ScrollPane scroll;
}
