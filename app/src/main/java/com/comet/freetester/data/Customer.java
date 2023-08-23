package com.comet.freetester.data;

import java.util.HashMap;

public class Customer {
    public String firstName;
    public String lastName;
    public String email;
    public String phone;

    public HashMap<String, Object> getDataMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("phone", phone);

        return result;
    }

    public static Customer fromMap(HashMap<String, Object> map) {
        Customer result = new Customer();

        result.firstName = (String) map.get("firstName");
        result.lastName = (String) map.get("lastName");
        result.email = (String) map.get("email");
        result.phone = (String) map.get("phone");

        return result;
    }

}
