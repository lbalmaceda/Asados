package com.auth0.authsados.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class Asado {

    public String finalDate;
    public String location;
    public String datePoll;
    public Map<String, Boolean> assistants;

    public Asado() {
        this(null, null);
    }

    public Asado(String location, String finalDate) {
        assistants = new HashMap<>();
        this.finalDate = finalDate;
        this.location = location;
    }

    public Asado(String location) {
        this(location, null);
    }

}
