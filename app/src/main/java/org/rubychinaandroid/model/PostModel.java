package org.rubychinaandroid.model;

import android.util.Log;

import org.json.JSONObject;

public class PostModel extends BaseModel {
    private TopicModel topic;
    private String body;
    private String bodyHtml;
    private String hits;

    public PostModel() {
        topic = new TopicModel();
        body = "这里是post的正文";
        bodyHtml = "这里是post的正文html";
        hits = "0";
    }

    public TopicModel getTopic() {
        return topic;
    }

    public void setTopic(TopicModel topic) {
        if (topic != null) {
            this.topic = topic;
        } else {
            Log.d("PM", "NULL TOPIC");
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public void parse(JSONObject jsonObject) {
        try {
            JSONObject joPost = jsonObject.getJSONObject("topic");
            this.topic.parse(joPost);

            this.body = joPost.getString("body");
            this.bodyHtml = joPost.getString("body_html");
            this.hits = joPost.getString("hits");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
