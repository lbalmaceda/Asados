package com.auth0.authsados.pojo;

import java.util.List;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class PollOption {
    public String date;
    public List<String> votes;

    public PollOption() {
    }

    public PollOption(String date) {
        this.date = date;
    }

    public void addVote(String userId) {
        if (!this.votes.contains(userId)) {
            this.votes.add(userId);
        }
    }

    public void removeVote(String userId) {
        if (this.votes.contains(userId)) {
            this.votes.remove(userId);
        }
    }
}
