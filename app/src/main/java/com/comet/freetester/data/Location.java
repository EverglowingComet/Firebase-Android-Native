package com.comet.freetester.data;

import com.comet.freetester.util.FirebaseUtils;

import java.util.HashMap;

public class Location {
    public String place_id;
    public String name;
    public String formatted_address;
    public String icon;
    public String icon_mask_base_uri;
    public String parkingInfo;
    public String unitNumber;
    public String storeNumber;
    public String url;
    public boolean isStore;
    public boolean needStairs;
    public boolean hardwood;
    public double lat;
    public double lng;

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("place_id", place_id);
        result.put("name", name);
        result.put("formatted_address", formatted_address);
        result.put("icon", icon);
        result.put("icon_mask_base_uri", icon_mask_base_uri);
        result.put("parkingInfo", parkingInfo);
        result.put("unitNumber", unitNumber);
        result.put("storeNumber", storeNumber);
        result.put("url", url);
        result.put("isStore", isStore);
        result.put("needStairs", needStairs);
        result.put("hardwood", hardwood);
        result.put("lat", lat);
        result.put("lng", lng);

        return result;
    }

    public static Location fromMap(HashMap<String, Object> map) {
        Location result = new Location();

        result.place_id = (String) map.get("place_id");
        result.name = (String) map.get("name");
        result.formatted_address = (String) map.get("formatted_address");
        result.icon = (String) map.get("icon");
        result.icon_mask_base_uri = (String) map.get("icon_mask_base_uri");
        result.parkingInfo = (String) map.get("parkingInfo");
        result.unitNumber = (String) map.get("unitNumber");
        result.storeNumber = (String) map.get("storeNumber");
        result.url = (String) map.get("url");
        result.isStore = FirebaseUtils.getBoolean(map, "isStore", false);
        result.needStairs = FirebaseUtils.getBoolean(map, "needStairs", false);
        result.hardwood = FirebaseUtils.getBoolean(map, "hardwood", false);
        result.lat = FirebaseUtils.getDouble(map, "lat", 0);
        result.lng = FirebaseUtils.getDouble(map, "lng", 0);

        return result;
    }
}
