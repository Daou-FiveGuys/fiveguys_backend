package com.precapstone.fiveguys_backend.message.send.option;

import java.util.HashMap;
import java.util.Map;

public class Files {
    private final String name;
    private final int size;
    private final String data;

    public Files(String name, int size, String data) {
        this.name = name;
        this.size = size;
        this.data = data;
    }

    public Map<String, Object> get() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("size", size);
        map.put("data", data);

        return map;
    }
}
