package li.dic.tourphone.domain.auth;

import androidx.annotation.Keep;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Login implements Serializable {

    @SerializedName("jwt")
    @Expose
    private String jwt;
    @SerializedName("user")
    @Expose
    private User user;
    private final static long serialVersionUID = -4976254329458426407L;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}