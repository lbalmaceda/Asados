package com.auth0.authsados.pojo;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class Asado {
//    public Map<String, String> createdAt;
    public String location;
    public DatePoll selectedDate;

    public Asado() {
    }

    public Asado(String location) {
//        this.createdAt = ServerValue.TIMESTAMP;
        this.location = location;
    }
}
