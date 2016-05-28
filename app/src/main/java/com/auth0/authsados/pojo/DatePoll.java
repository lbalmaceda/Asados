package com.auth0.authsados.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class DatePoll {
    public String date;
    public Map<String, Boolean> assistants;

    public DatePoll() {
        this.assistants = new HashMap<>();
    }

    public DatePoll(String date) {
        this.date = date;
        this.assistants = new HashMap<>();
    }

    public void addVote(String userId) {
        this.assistants.put(userId, true);
    }

    public void removeVote(String userId) {
        this.assistants.remove(userId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("assistants", assistants);
        return map;
    }

    public void toggleVote(String userId) {
        if (this.assistants.containsKey(userId)) {
            this.assistants.remove(userId);
        } else {
            this.assistants.put(userId, true);
        }
    }
}
