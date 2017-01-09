package model;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedListener;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Represents the common attributes/behavior to
 */
public abstract class Agent implements Runnable, MessageReceivedListener {

    /**
     * List of all agents ever created
     */
    public static HashMap<Integer, Agent> agents = new HashMap<Integer, Agent>();
    public static int MAX_NB_PROPOSITIONS = 6;

    String name;
    int Id;
    protected HashMap<Integer, Integer> nbPropositions;
    AgentSocket agentSocket;

    public Agent(String name, int id) {
        this.name = name;
        Id = id;
        nbPropositions = new HashMap<>();
        agents.put(id, this);
    }


    public void sendMessage(int recipient, String msg) {
        if (recipient != this.getId() && agents.containsKey(recipient)) {
            this.agentSocket.sendMessage(recipient, msg);
        }
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

    public int getNbPropositions(Agent agent) {
        if (!nbPropositions.containsKey(agent.getId()))
            nbPropositions.put(agent.getId(), 0);
        return nbPropositions.get(agent.getId());
    }

    public void setNbPropositions(Agent agent, int nbPropositions) {
        this.nbPropositions.put(agent.getId(), nbPropositions);
    }

    public void incrementNbPropositions(Agent agent) {
        this.nbPropositions.put(agent.getId(), this.nbPropositions.get(agent.getId()) + 1);
    }
}
