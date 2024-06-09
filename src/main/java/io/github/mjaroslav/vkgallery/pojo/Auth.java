package io.github.mjaroslav.vkgallery.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("expires_in")
    private Long expiresIn;
}
