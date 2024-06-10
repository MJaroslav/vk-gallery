package io.github.mjaroslav.vkgallery.util;

import io.github.mjaroslav.vkgallery.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class FXMLUtils {
    public @NotNull <V extends Parent, C extends Controller> Pair<@NotNull V, @Nullable C> loadFXML(
        @NotNull String name) throws IOException {
        val loader = new FXMLLoader();
        V view = loader.load(
            ResourceManager.getAsStreamAbsolute("io/github/mjaroslav/vkgallery/view/" + name + "View" + ".fxml"));
        C controller = loader.getController();
        return Pair.of(view, controller);
    }

    public @NotNull <V extends Parent, C extends Controller> Pair<@NotNull V, @Nullable C> loadAndSetScene(
        @NotNull Stage stage, @NotNull String name) throws IOException {
        Pair<V, C> pair = loadFXML(name);
        val orig = stage.getScene();
        val w = orig == null ? 600 : stage.getScene().getWidth();
        val h = orig == null ? 400 : stage.getScene().getHeight();
        val scene = new Scene(pair.getLeft(), w, h);
        stage.setScene(scene);
        pair.getRight().postInitialize();
        return pair;
    }

    public @NotNull List<@NotNull Node> getVisibleNodes(@NotNull ScrollPane pane) {
        val result = new ArrayList<@NotNull Node>();
        val bounds = pane.localToScene(pane.getBoundsInParent());
        if (pane.getContent() instanceof Parent content) for (var node : content.getChildrenUnmodifiable())
            if (bounds.intersects(node.localToScene(node.getBoundsInLocal()))) result.add(node);
        return result;
    }
}
