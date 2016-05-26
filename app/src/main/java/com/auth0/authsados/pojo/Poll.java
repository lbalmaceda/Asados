package com.auth0.authsados.pojo;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class Poll {
    public Map<String, String> created;
    public List<PollOption> options;

    public Poll() {
        this(new ArrayList<PollOption>());
    }

    public Poll(List<PollOption> dates) {
        this.created = ServerValue.TIMESTAMP;
        this.options = dates;
    }
}
