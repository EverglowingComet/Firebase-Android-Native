package com.comet.freetester.data;

import android.text.TextUtils;

import com.comet.freetester.util.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Delivery {
    public String id;
    public String orderId;
    public String payment_intent;
    public String note;
    public String deliveryPhotoUri;
    public boolean delivered;
    public boolean completed;
    public boolean paid;
    public double price;
    public long deadline;
    public Customer customer;
    public Location store;
    public Location dropoff;
    public ArrayList<Item> items;

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("orderId", orderId);
        result.put("payment_intent", payment_intent);
        result.put("note", note);
        result.put("deliveryPhotoUri", deliveryPhotoUri);
        result.put("delivered", delivered);
        result.put("completed", completed);
        result.put("paid", paid);
        result.put("price", price);
        result.put("deadline", deadline);
        result.put("customer", customer.getDataMap());
        HashMap<String, Object> map = new HashMap<>();
        if (dropoff != null) {
            map.put("dropoff", dropoff.getDataMap());
        }
        if (store != null) {
            map.put("store", store.getDataMap());
        }
        result.put("location", map);
        result.put("items", FirebaseUtils.getItemListMap(items));

        return result;
    }

    public static Delivery fromMap(HashMap<String, Object> map) {
        Delivery result = new Delivery();

        result.id = (String) map.get("id");
        result.orderId = (String) map.get("orderId");
        result.payment_intent = (String) map.get("payment_intent");
        result.note = (String) map.get("note");
        result.deliveryPhotoUri = (String) map.get("deliveryPhotoUri");
        result.delivered = FirebaseUtils.getBoolean(map, "delivered", false);
        result.completed = FirebaseUtils.getBoolean(map, "completed", false);
        result.paid = FirebaseUtils.getBoolean(map, "paid", false);
        result.price = FirebaseUtils.getDouble(map, "price", 0);
        result.deadline = FirebaseUtils.getLong(map, "deadline", 0);
        if (map.containsKey("customer")) {
            result.customer = Customer.fromMap((HashMap<String, Object>) map.get("customer"));
        }
        if (map.containsKey("location")) {
            HashMap<String, Object> dict = (HashMap<String, Object>) map.get("location");
            if (dict != null && dict.containsKey("store")) {
                result.store = Location.fromMap((HashMap<String, Object>) dict.get("store"));
            }
            if (dict != null && dict.containsKey("dropoff")) {
                result.dropoff = Location.fromMap((HashMap<String, Object>) dict.get("dropoff"));
            }
        }
        result.items = FirebaseUtils.getItemList(map, "items");

        return result;
    }

    public String getNameStr() {
        String result = "";
        result += TextUtils.isEmpty(customer.firstName) ? "" : customer.firstName;
        if (result.length() > 0) {
            result += " ";
        }
        result += TextUtils.isEmpty(customer.lastName) ? "" : customer.lastName;

        return result;
    }
}
