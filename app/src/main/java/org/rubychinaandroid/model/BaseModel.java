package org.rubychinaandroid.model;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseModel {
    abstract public void parse(JSONObject jsonObject) throws JSONException;
}
