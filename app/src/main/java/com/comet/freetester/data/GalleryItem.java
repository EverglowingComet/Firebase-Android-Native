package com.comet.freetester.data;


import com.comet.freetester.util.FirebaseUtils;

import java.util.HashMap;

public class GalleryItem {
    public String id;
    public String uid;
    public String title;
    public String note;
    public String photoUri;
    public boolean delivered;
    public boolean completed;
    public boolean paid;
    public double price;
    public long createdAt;

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("uid", uid);
        result.put("title", title);
        result.put("note", note);
        result.put("photoUri", photoUri);
        result.put("delivered", delivered);
        result.put("completed", completed);
        result.put("paid", paid);
        result.put("price", price);
        result.put("createdAt", createdAt);

        return result;
    }

    public static GalleryItem fromMap(HashMap<String, Object> map) {
        GalleryItem result = new GalleryItem();

        result.id = (String) map.get("id");
        result.uid = (String) map.get("uid");
        result.title = (String) map.get("title");
        result.note = (String) map.get("note");
        result.photoUri = (String) map.get("photoUri");
        result.delivered = FirebaseUtils.getBoolean(map, "delivered", false);
        result.completed = FirebaseUtils.getBoolean(map, "completed", false);
        result.paid = FirebaseUtils.getBoolean(map, "paid", false);
        result.price = FirebaseUtils.getDouble(map, "price", 0);
        result.createdAt = FirebaseUtils.getLong(map, "createdAt", 0);

        return result;
    }
}
