package org.rubychinaandroid.api;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rubychinaandroid.model.BaseModel;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yw on 2015/5/2.
 */
class WrappedTextHttpResponseHandler<T extends BaseModel> extends JsonHttpResponseHandler {
    RubyChinaApiListener<ArrayList<T>> handler;
    Class c;
    String jsonObjName;

    public WrappedTextHttpResponseHandler(Class c, String name,
                                          RubyChinaApiListener<ArrayList<T>> handler) {
        this.handler = handler;
        this.c = c;
        jsonObjName = name;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        try {
            JSONArray jsonArray = response.getJSONArray(jsonObjName);

            ArrayList<T> models = new ArrayList<T>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    T obj = (T) Class.forName(c.getName()).newInstance();
                    obj.parse(jsonObj);
                    if (obj != null)
                        models.add(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //PersistenceHelper.saveModelList(context, models, key);
            SafeHandler.onSuccess(handler, models);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
        handleFailure(statusCode, e.getMessage());
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
        handleFailure(statusCode, e.getMessage());
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
        handleFailure(statusCode, e.getMessage());
    }

    private void handleFailure(int statusCode, String error) {
        //error = V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorApiForbidden);
        SafeHandler.onFailure(handler, error);
    }
}
