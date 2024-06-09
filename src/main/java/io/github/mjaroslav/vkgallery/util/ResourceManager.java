package io.github.mjaroslav.vkgallery.util;

import lombok.experimental.UtilityClass;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import static io.github.mjaroslav.vkgallery.References.*;

@UtilityClass
public class ResourceManager {
    private final AppDirs DIRS = AppDirsFactory.getInstance();

    public @NotNull URL getAbsolute(@NotNull String path) {
        return Objects.requireNonNull(ResourceManager.class.getResource(StringUtils.prependIfMissing(path, "/")));
    }

    public @NotNull InputStream getAsStreamAbsolute(@NotNull String path) {
        return Objects.requireNonNull(
            ResourceManager.class.getResourceAsStream(StringUtils.prependIfMissing(path, "/")));
    }

    public @NotNull Path getConfigDirectory() {
        if (System.getenv().containsKey(APP_HOME_ENV)) return Path.of(System.getenv(APP_HOME_ENV));
        else return Path.of(DIRS.getUserConfigDir(APP_NAME, VERSION, AUTHOR));
    }

    public @NotNull Path getCacheDirectory() {
        return Path.of(DIRS.getUserCacheDir(APP_NAME, VERSION, AUTHOR));
    }
}
