package model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Agent {

    public static HashMap<Integer, Agent> agents = new HashMap<Integer, Agent>();


    public static Agent createFromJSON(JSONObject jsonObject) {
        return new Agent(jsonObject.getString("name"), jsonObject.getInt("Id"));
    };

    String name;
    int Id;
    protected int nbPropositions;

    public Agent(String name, int id) {
        this.name = name;
        Id = id;
        nbPropositions = 0;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", Id:" + Id +
                '}';
    }

    public int getNbPropositions() {
        return nbPropositions;
    }

    public void setNbPropositions(int nbPropositions) {
        this.nbPropositions = nbPropositions;
    }
}
