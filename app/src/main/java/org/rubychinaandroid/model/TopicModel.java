package org.rubychinaandroid.model;


import org.json.JSONException;
import org.json.JSONObject;
import org.rubychinaandroid.utils.RubyChinaTypes;
import org.rubychinaandroid.utils.Utility;

/* JSON Model */
public class TopicModel extends BaseModel {

    private static final String TIME_FORMAT = "创建于 %s";
    private static final String DETAIL_FORMAT = "%s " + TIME_FORMAT;

    public String getTopicId() {
        return id;
    }

    public void setTopicId(String topicId) {
        this.id = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(String repliedAt) {
        this.repliedAt = repliedAt;
    }

    public String getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(String repliesCount) {
        this.repliesCount = repliesCount;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLastReplyUserId() {
        return lastReplyUserId;
    }

    public void setLastReplyUserId(String lastReplyUserId) {
        this.lastReplyUserId = lastReplyUserId;
    }

    public String getLastReplyUserLogin() {
        return lastReplyUserLogin;
    }

    public void setLastReplyUserLogin(String lastReplyUserLogin) {
        this.lastReplyUserLogin = lastReplyUserLogin;
    }

    public String getDetail() {
        return String.format(DETAIL_FORMAT,
                "".equals(user.getName()) ? user.getLogin() : user.getName(), getCreatedTime());
    }

    public String getCreatedTime() {
        return Utility.getTimeSpanSinceCreated(createdAt);
    }

    public void setUserLogin(String login) {
        user.setLogin(login);
    }

    public String getUserLogin() {
        return user.getLogin();
    }

    public void setUserName(String name) {
        user.setName(name);
    }

    public String getUserName() {
        return user.getName();
    }

    public void setUserAvatarUrl(String avatarUrl) {
        this.user.setAvatarUrl(avatarUrl);
    }

    public String getUserAvatarUrl() {
        return user.getAvatarUrl();
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RubyChinaTypes.TOPIC_CATEGORY getCategory() {
        return category;
    }

    public void setCategory(RubyChinaTypes.TOPIC_CATEGORY category) {
        this.category = category;
    }

    private String id;
    private String title;
    private String createdAt;
    private String updatedAt;
    private String repliedAt;
    private String repliesCount;
    private String nodeName;
    private String nodeId;
    private String lastReplyUserId;
    private String lastReplyUserLogin;

    private User user;

    boolean deleted;

    Abilities abilities;

    RubyChinaTypes.TOPIC_CATEGORY category;

    public TopicModel() {
        this.user = new User();
        this.abilities = new Abilities();
    }

    public void parse(JSONObject jsonObject) throws JSONException {

        this.id = jsonObject.getString("id");
        this.title = jsonObject.getString("title");
        this.createdAt = jsonObject.getString("created_at");
        this.updatedAt = jsonObject.getString("updated_at");
        this.repliedAt = jsonObject.getString("replied_at");
        this.repliesCount = jsonObject.getString("replies_count");
        this.nodeName = jsonObject.getString("node_name");
        this.nodeId = jsonObject.getString("node_id");
        this.lastReplyUserId = jsonObject.getString("last_reply_user_id");
        this.lastReplyUserLogin = jsonObject.getString("last_reply_user_login");

        JSONObject joUser = jsonObject.getJSONObject("user");
        this.user.setId(joUser.getString("id"));
        this.user.setLogin(joUser.getString("login"));
        this.user.setName(joUser.getString("name"));
        this.user.setAvatarUrl(joUser.getString("avatar_url"));

        this.deleted = jsonObject.getBoolean("deleted");

        JSONObject abilities = jsonObject.getJSONObject("abilities");
        this.abilities.setUpdate(abilities.getBoolean("update"));
        this.abilities.setDestroy(abilities.getBoolean("destroy"));
    }
}
