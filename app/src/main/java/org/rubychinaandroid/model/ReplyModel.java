package org.rubychinaandroid.model;


import org.json.JSONObject;
import org.rubychinaandroid.utils.Utility;

public class ReplyModel extends BaseModel {

    private String replyId;
    private String bodyHtml;
    private String createdAt;
    private String updatedAt;
    private boolean deleted;
    private String topicId;

    private User user;

    private Abilities abilities;

    public ReplyModel() {
        this.user = new User();
        this.abilities = new Abilities();
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String id) {
        replyId = id;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return user.getId();
    }

    public void setUserId(String userId) {
        user.setId(userId);
    }

    public String getUserLogin() {
        return user.getLogin();
    }

    public void setUserLogin(String userLogin) {
        user.setLogin(userLogin);
    }

    public String getUserName() {
        return user.getName();
    }

    public void setUserName(String userName) {
        user.setName(userName);
    }

    public String getUserAvatarUrl() {
        return user.getAvatarUrl();
    }

    public void setUserAvatarUrl(String url) {
        user.setAvatarUrl(url);
    }

    public boolean getUpdate() {
        return abilities.getUpdate();
    }

    public void setUpdate(boolean update) {
        abilities.setUpdate(update);
    }

    public boolean getDestroy() {
        return abilities.getDestroy();
    }

    public void setDestroy(boolean destroy) {
        abilities.setDestroy(destroy);
    }

    public void parse(JSONObject jsonObject) {
        try {
            this.replyId = jsonObject.getString("id");
            this.bodyHtml = jsonObject.getString("body_html");
            this.createdAt = jsonObject.getString("created_at");
            this.updatedAt = jsonObject.getString("updated_at");
            this.deleted = jsonObject.getBoolean("deleted");
            this.topicId = jsonObject.getString("topic_id");

            JSONObject user = jsonObject.getJSONObject("user");
            this.user.setId(user.getString("id"));
            this.user.setLogin(user.getString("login"));
            this.user.setName(user.getString("name"));
            this.user.setAvatarUrl(user.getString("avatar_url"));

            JSONObject joAbilities = jsonObject.getJSONObject("abilities");
            abilities.setUpdate(joAbilities.getBoolean("update"));
            abilities.setDestroy(joAbilities.getBoolean("destroy"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCreatedTime() {
        return Utility.getTimeSpanSinceCreated(createdAt);
    }
}
