package com.comet.freetester.data;

import com.comet.freetester.util.FirebaseUtils;

import java.util.HashMap;

public class Item {
    public String id;
    public String title;
    public String photoUri;
    public String other;
    public int count;
    public boolean assemblyFull;
    public boolean breakdownRequired;
    public double width;
    public double height;
    public double depth;
    public double weight;

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("title", title);
        result.put("photoUri", photoUri);
        result.put("other", other);
        result.put("count", count);
        result.put("assemblyFull", assemblyFull);
        result.put("breakdownRequired", breakdownRequired);
        result.put("width", width);
        result.put("height", height);
        result.put("depth", depth);
        result.put("weight", weight);

        return result;
    }

    public static Item fromMap(HashMap<String, Object> map) {
        Item result = new Item();

        result.id = (String) map.get("id");
        result.title = (String) map.get("title");
        result.photoUri = (String) map.get("photoUri");
        result.other = (String) map.get("other");
        result.count = FirebaseUtils.getInteger(map, "count", 0);
        result.assemblyFull = FirebaseUtils.getBoolean(map, "assemblyFull", false);
        result.breakdownRequired = FirebaseUtils.getBoolean(map, "breakdownRequired", false);
        result.width = FirebaseUtils.getDouble(map, "width", 0);
        result.height = FirebaseUtils.getDouble(map, "height", 0);
        result.depth = FirebaseUtils.getDouble(map, "depth", 0);
        result.weight = FirebaseUtils.getDouble(map, "weight", 0);

        return result;
    }
}
