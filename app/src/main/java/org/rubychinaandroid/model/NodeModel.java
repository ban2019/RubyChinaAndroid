package org.rubychinaandroid.model;


import org.json.JSONObject;

public class NodeModel extends BaseModel {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void parse(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.id = jsonObject.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
