package io.github.mjaroslav.vkgallery.vk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.vk.api.sdk.objects.photos.responses.GetAlbumsResponse;
import com.vk.api.sdk.objects.photos.responses.GetResponse;
import io.github.mjaroslav.vkgallery.VKGallery;
import io.github.mjaroslav.vkgallery.pojo.Auth;
import io.github.mjaroslav.vkgallery.util.FXMLUtils;
import io.github.mjaroslav.vkgallery.util.ResourceManager;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Getter
public class VKHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path TOKEN_FILE = ResourceManager.getConfigDirectory().resolve("token");

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    // VK Admin auth token
    public static final String AUTH_URL =
        "https://oauth.vk.com/authorize?client_id=6121396&scope=65540" + "&redirect_uri=" + REDIRECT_URL
            + "&display=page&response_type=token&revoke=1";

    private VkApiClient vk;
    private UserActor actor;

    public void login(@NotNull Stage stage) throws IOException {
        if (isLoggedOn()) return;
        val client = new HttpTransportClient();
        vk = new VkApiClient(client);
        if (PathUtils.isRegularFile(TOKEN_FILE)) {
            val response =
                GSON.fromJson(FileUtils.readFileToString(TOKEN_FILE.toFile(), StandardCharsets.UTF_8), Auth.class);
            actor = new UserActor(response.getUserId(), response.getAccessToken());
            FXMLUtils.changeScene(stage, "Main");
        }
        else FXMLUtils.changeScene(stage, "Login");
    }

    @SneakyThrows
    public void onLoginCallback(@NotNull String url) {
        if (!url.startsWith(REDIRECT_URL)) return;
        String accessToken = null;
        long userId = 0;
        long expiresAt = 0;
        for (var pair : url.split("#")[1].split("&")) {
            val split = pair.split("=");
            switch (split[0]) {
                case "access_token" -> accessToken = split[1];
                case "expires_in" -> expiresAt = Long.parseLong(split[1]);
                case "user_id" -> userId = Long.parseLong(split[1]);
            }
        }
        if (StringUtils.isNotEmpty(accessToken) && userId > 0) {
            actor = new UserActor(userId, accessToken);
            val response = new Auth(accessToken, userId, expiresAt);
            PathUtils.createParentDirectories(TOKEN_FILE);
            FileUtils.write(TOKEN_FILE.toFile(), GSON.toJson(response), StandardCharsets.UTF_8);
            FXMLUtils.changeScene(VKGallery.PRIMARY_STAGE, "Album");
        }
    }

    public boolean isLoggedOn() {
        return vk != null && actor != null;
    }

    @SneakyThrows
    public GetAlbumsResponse getAlbums() {
        val req = vk.photos().getAlbums(actor).needCovers(true).photoSizes(true).needSystem(true);
        val result = req.executeAsString();
        return req.execute();
    }

    @SneakyThrows
    public GetResponse getPhotos(int album) {
        return vk.photos().get(actor).rev(true).albumId(album + "").count(1000).photoSizes(true).execute();
    }

    @SneakyThrows
    public void getAllPhotos(PhotoAlbumFull album, @NotNull TriConsumer<Integer, Integer, List<Photo>> action) {
        var offset = 0;
        GetResponse response;
        do {
            response = vk.photos().get(actor).rev(true).albumId(album.getId() + "").offset(offset * 1000).count(1000)
                .photoSizes(true).execute();
            if (!response.getItems().isEmpty()) action.accept(offset + 1, (int)Math.ceil(album.getSize() / 1000d),
                response.getItems());
            offset++;
        } while (response.getItems().size() == 1000);
    }
}
