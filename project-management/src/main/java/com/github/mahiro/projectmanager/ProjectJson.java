package com.github.mahiro.projectmanager;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectJson {

    private ProjectJson() {
    }

    public static JSONObject toJson(Project project) {
        JSONObject json = new JSONObject();
        json.put("id", project.getId());
        json.put("title", project.getTitle());
        json.put("clientName", project.getClientName());
        json.put("requiredSkills", project.getRequiredSkills());
        json.put("location", project.getLocation());
        json.put("priceMin", project.getPriceMin());
        json.put("priceMax", project.getPriceMax());
        json.put("status", project.getStatus());
        json.put("applied", project.isApplied());
        json.put("category", project.getCategory());
        json.put("createdAt", project.getCreatedAt() == null ? JSONObject.NULL : project.getCreatedAt().toInstant().toString());
        json.put("updatedAt", project.getUpdatedAt() == null ? JSONObject.NULL : project.getUpdatedAt().toInstant().toString());
        return json;
    }

    public static JSONArray toJsonArray(java.util.List<Project> projects) {
        JSONArray array = new JSONArray();
        for (Project p : projects) {
            array.put(toJson(p));
        }
        return array;
    }

    // JSONの値が無ければ空文字を返す（ProjectServiceの引数(String)が空文字="変更なし/未指定"を期待するため）
    public static String optString(JSONObject json, String key) {
        return json.has(key) && !json.isNull(key) ? String.valueOf(json.get(key)) : "";
    }
}
