package com.comet.freetester.core.remote.data;

import com.comet.freetester.util.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class UserProfile {
    public String uid;
    public String username;
    public String email;
    public String location;
    public String photoUri;
    public @Nullable String firstName;
    public @Nullable String lastName;
    public @Nullable String country;
    public @Nullable String countryCode;
    public @Nullable String phoneNumber;
    public @Nullable String gender;
    public @Nullable String bio;
    public double weight;
    public long birthday;
    public boolean metricUnits = true;

    public ArrayList<String> followerIds = new ArrayList<>();
    public ArrayList<String> pendingIds = new ArrayList<>();

    public UserProfile(String uid) {
        this.uid = uid;
    }

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("location", location);
        result.put("photoUri", photoUri);
        result.put("followerIds", FirebaseUtils.getStringListMap(followerIds));
        result.put("pendingIds", FirebaseUtils.getStringListMap(pendingIds));

        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("country", country);
        result.put("countryCode", countryCode);
        result.put("phoneNumber", phoneNumber);
        result.put("gender", gender);
        result.put("bio", bio);
        result.put("weight", weight);
        result.put("metricUnits", metricUnits);
        result.put("birthday", birthday);

        return result;
    }

    public static UserProfile fromMap(HashMap<String, Object> map) {
        String uid = (String) map.get("uid");
        UserProfile result = new UserProfile(uid);

        result.username = (String) map.get("username");
        if (result.username == null) {
            result.username = "Unknown";
        }
        result.email = (String) map.get("email");
        result.location = (String) map.get("location");
        result.photoUri = (String) map.get("photoUri");
        result.followerIds = FirebaseUtils.getStringList(map, "followerIds");
        result.pendingIds = FirebaseUtils.getStringList(map, "pendingIds");

        result.firstName = (String) map.get("firstName");
        result.lastName = (String) map.get("lastName");
        result.country = (String) map.get("country");
        result.countryCode = (String) map.get("countryCode");
        result.phoneNumber = (String) map.get("phoneNumber");
        result.gender = (String) map.get("gender");
        result.bio = (String) map.get("bio");
        result.weight = FirebaseUtils.getDouble(map, "weight", 0);
        result.birthday = FirebaseUtils.getLong(map, "birthday", 0);
        result.metricUnits = FirebaseUtils.getBoolean(map, "metricUnits", true);

        return result;
    }

    public int completePercentage() {
        int result = 10;
        if (photoUri != null) {
            result += 20;
        }
        if (firstName != null) {
            result += 10;
        }
        if (lastName != null) {
            result += 10;
        }
        if (country != null) {
            result += 10;
        }
        if (phoneNumber != null) {
            result += 10;
        }
        if (birthday > 0) {
            result += 10;
        }
        if (weight > 0) {
            result += 10;
        }
        if (gender != null) {
            result += 10;
        }

        return result;
    }

    public String getNameStr() {
        if (firstName != null) {
            return firstName;
        }
        return username;
    }
}
