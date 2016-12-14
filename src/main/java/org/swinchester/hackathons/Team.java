package org.swinchester.hackathons;

import java.util.HashMap;

/**
 * Created by swinchester on 14/12/2016.
 */
public class Team {

    private String teamName;
    private String reindeerName;
    private HashMap<String,String> nameEmailMap;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getReindeerName() {
        return reindeerName;
    }

    public void setReindeerName(String reindeerName) {
        this.reindeerName = reindeerName;
    }

    public HashMap<String, String> getNameEmailMap() {
        return nameEmailMap;
    }

    public void setNameEmailMap(HashMap<String, String> nameEmailMap) {
        this.nameEmailMap = nameEmailMap;
    }
}
