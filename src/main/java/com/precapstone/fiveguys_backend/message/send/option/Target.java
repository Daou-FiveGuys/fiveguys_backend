package com.precapstone.fiveguys_backend.message.send.option;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class Target {
    private final String toNumber;
    private final String name;
    private final ChangeWord changeWord;

    public Target(String toNumber, String name, ChangeWord changeWord){
        this.toNumber = toNumber;
        this.name = name;
        this.changeWord = changeWord;
    }

    @JsonValue
    public Map<String, Object> get() {
        Map<String, Object> map = new HashMap<>();

        System.out.println(toNumber);
        map.put("to", toNumber);
        map.put("name", name);
        map.put("changeWord", changeWord);

        return map;
    }
}
