package org.rubychinaandroid.model;


import org.json.JSONObject;

public class NodeModel extends BaseModel {
    private String id;
    private String name;
    private int topicsCount;
    private String summary;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTopicsCount() {
        return topicsCount;
    }

    public String getSummary() {
        return summary;
    }


    public void parse(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.id = jsonObject.getString("id");
            this.topicsCount = jsonObject.getInt("topics_count");
            this.summary = jsonObject.getString("summary");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
