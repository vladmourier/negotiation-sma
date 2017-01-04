package model;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Represents the common attributes/behavior to
 */
public class Agent {

    /**
     * List of all agents ever created
     */
    public static HashMap<Integer, Agent> agents = new HashMap<Integer, Agent>();

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
