package io.github.mjaroslav.vkgallery.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    private int columns = 5;
    private int rows = 5;
    private int previewSize = 200;

    public int getPageSize() {
        return columns * rows;
    }
}
