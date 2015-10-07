package org.rubychinaandroid.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    private String userName;
    private String userLogin;
    private String avatarUrl;
    private String email;

    public void parse(JSONObject jsonObject) throws JSONException {

        JSONObject joUser = jsonObject.getJSONObject("user");
        this.userName = joUser.getString("name");
        this.userLogin = joUser.getString("login");
        this.avatarUrl = joUser.getString("avatar_url");
        if (joUser.has("email")) {
            this.email = joUser.getString("email");
        }

        Log.d("usermodel", userLogin);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        if ("".equals(userName)) {
            return userLogin;
        }

        return userName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
