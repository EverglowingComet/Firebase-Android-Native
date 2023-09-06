package com.comet.freetester.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Nullable;

public class FirebaseUtils {

    public static String getStringFromStringArray(ArrayList<String> arrayList) {
        StringBuilder result = new StringBuilder();
        for (String item : arrayList) {
            if (!result.toString().equals("")) {
                result.append(",");
            }
            result.append(item);
        }
        return result.toString();
    }

    public static ArrayList<String> getStringArrayFromString(@Nullable String str) {
        ArrayList<String> result = new ArrayList<String>();
        if (str != null) {
            result.addAll(Arrays.asList(str.split(",")));
        }
        return result;
    }

    public static int getInteger(HashMap<String, Object> map, String key, int defaultValue) {
        if (map.containsKey(key)) {
            try {
                return Integer.parseInt(map.get(key).toString());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static double getDouble(HashMap<String, Object> map, String key, double defaultValue) {
        if (map.containsKey(key)) {
            try {
                return Double.parseDouble(map.get(key).toString());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static float getFloat(HashMap<String, Object> map, String key, float defaultValue) {
        if (map.containsKey(key)) {
            try {
                return Float.parseFloat(map.get(key).toString());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static boolean getBoolean(HashMap<String, Object> map, String key, boolean defaultValue) {
        if (map.containsKey(key)) {
            try {
                return Boolean.parseBoolean(map.get(key).toString());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static long getLong(HashMap<String, Object> map, String key, long defaultValue) {
        if (map.containsKey(key)) {
            try {
                return Long.parseLong(map.get(key).toString());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static ArrayList<String> getStringList(HashMap<String, Object> map, String key) {
        ArrayList<String> result = new ArrayList<>();

        try {
            HashMap<String, String> data = (HashMap<String, String>) map.get(key);

            for (String item : data.keySet()) {
                result.add(item);
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, Boolean> getStringBoolMap(HashMap<String, Object> map, String key) {
        HashMap<String, Boolean> result = new HashMap<>();

        try {
            HashMap<String, Boolean> data = (HashMap<String, Boolean>) map.get(key);

            return data;
        } catch (Exception e) {
            return result;
        }
    }

    public static HashMap<String, String> getStringListMap(ArrayList<String> data) {
        HashMap<String, String> result = new HashMap<>();

        try {
            for (String item : data) {
                result.put(item, item);
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, ArrayList<String>> getStringListMap(HashMap map, String key) {
        HashMap<String, ArrayList<String>> result = new HashMap<>();

        try {
            HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>) map.get(key);

            for (String id : data.keySet()) {
                HashMap<String, String> item = data.get(id);
                ArrayList<String> items = new ArrayList<>();
                for (String itemId: item.keySet()) {
                    items.add(itemId);
                }
                result.put(id, items);
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, HashMap<String, String>> getStringMapListMap(HashMap<String, ArrayList<String>> data) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        try {
            for (String id : data.keySet()) {
                HashMap<String, String> items = new HashMap<>();
                for (String key : data.get(id)) {
                    items.put(key, key);
                }
                result.put(id, items);
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, String> getStrStrList(HashMap<String, Object> map, String key) {
        HashMap<String, String> result = new HashMap<String, String>();

        try {
            HashMap<String, Object> data = (HashMap<String, Object>) map.get(key);

            for (String item : data.keySet()) {
                result.put(item, data.get(item).toString());
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, Integer> getStrIntegerList(HashMap<String, Object> map, String key) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        try {
            HashMap<String, Object> data = (HashMap<String, Object>) map.get(key);

            for (String item : data.keySet()) {
                result.put(item, Integer.parseInt(data.get(item).toString()));
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static HashMap<String, Double> getStrDoubleList(HashMap<String, Object> map, String key) {
        HashMap<String, Double> result = new HashMap<String, Double>();

        try {
            HashMap<String, Object> data = (HashMap<String, Object>) map.get(key);

            for (String item : data.keySet()) {
                result.put(item, Double.parseDouble(data.get(item).toString()));
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static boolean checkData(HashMap<String, Object> map, String key, Object value) {
        if (value != null && map.containsKey(key) && map.get(key).equals(value)) {
            return true;
        }
        return false;
    }

    public static double getDoubleStr(String str, int index, double defaultValue) {
        try {
            return Double.parseDouble(str.split(";")[index]);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long getLongStr(String str, int index, long defaultValue) {
        try {
            return Long.parseLong(str.split(";")[index]);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getIntegerStr(String str, int index, int defaultValue) {
        try {
            return Integer.parseInt(str.split(";")[index]);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getStr(String str, int index, String defaultValue) {
        try {
            return str.split(";")[index];
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
