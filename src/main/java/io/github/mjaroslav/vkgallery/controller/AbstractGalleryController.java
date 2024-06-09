package io.github.mjaroslav.vkgallery.controller;

import io.github.mjaroslav.vkgallery.util.FXMLUtils;
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
        root.widthProperty()
            .addListener((observable, oldValue, newValue) -> pane.setPrefColumns(newValue.intValue() / 200));
        scroll.setOnScroll(event -> {
            val current = FXMLUtils.getVisibleNodes(scroll);
            preDisplayNodes.stream().filter(n -> !current.contains(n)).forEach(this::onNodeOutFromDisplay);
            current.stream().filter(n -> !preDisplayNodes.contains(n)).forEach(this::onNodeInToDisplay);
            preDisplayNodes.clear();
            preDisplayNodes.addAll(current);
        });
        initNodes(pane);
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
