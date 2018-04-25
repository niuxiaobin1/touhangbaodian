package com.xinyi.touhang.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/25.
 */

public class JsonUtils {


    public static List<Map<String, String>> ArrayToList(JSONArray array, String[] keys) throws JSONException {

        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Map<String, String> map = new HashMap<>();
            JSONObject obj = array.getJSONObject(i);
            for (int j = 0; j < keys.length; j++) {
                map.put(keys[j], obj.getString(keys[j]));
            }
            list.add(map);
        }
        return list;
    }
}
