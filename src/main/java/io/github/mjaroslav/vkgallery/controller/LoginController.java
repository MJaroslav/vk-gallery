package io.github.mjaroslav.vkgallery.controller;

import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.vk.VKHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        web.getEngine().load(VKHelper.AUTH_URL);
        web.getEngine().locationProperty()
            .addListener(value -> VKGallery.VK.onLoginCallback(web.getEngine().getLocation()));
    }

    @FXML
    public WebView web;
}
