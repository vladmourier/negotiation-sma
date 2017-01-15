package model;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.events.MessageReceivedListener;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.ArrayList;
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
    protected HashMap<Agent, Ticket> lastBestOffer = new HashMap<>();
    protected Double lowThreshold;
    protected Double highThreshold;
    AgentSocket agentSocket;

    public Agent(String name, int id, double lowThreshold, double highThreshold) {
        this.name = name;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        Id = id;
        nbPropositions = new HashMap<>();
        agents.put(id, this);
    }


    public void sendMessage(int recipient, String msg) {
        if (recipient != this.getId() && agents.containsKey(recipient)) {
            this.agentSocket.sendMessage(recipient, msg);
        }
    }

    protected abstract boolean isBestOffer(Agent agent, Ticket ticket);

    protected void setLastBestOffer(Agent agent, Ticket ticket) {
        this.lastBestOffer.put(agent, ticket);
    }

    protected Ticket getLastBestOffer(Agent agent) {
        return this.lastBestOffer.get(agent);
    }

    protected abstract boolean isCorrectDeal(Ticket ticket);

    protected abstract boolean isGreatDeal(Ticket ticket);

    protected abstract boolean isPoorDeal(Ticket ticket);

    public void messageReceived(MessageReceivedEvent event) {
        Message receivedMessage = event.getSource().getLastReceivedMessage();
        boolean sendMessage = true;
        Ticket proposedTicket = receivedMessage.getTicket();
        Ticket nextTicket = new Ticket(proposedTicket.getId() + 1, proposedTicket.getFlight(), proposedTicket.getPrice(), proposedTicket.getDate());
        Action nextAction = Action.ORDER;
        System.out.println(receivedMessage);

        if (receivedMessage.isWellFormed() && getNbPropositions(receivedMessage.getEmitter()) <= MAX_NB_PROPOSITIONS) {
            ArrayList<Object> pack = treatMessageAccordingToAction(receivedMessage, nextAction, proposedTicket, nextTicket, sendMessage);
            nextAction = (Action) pack.get(0);
            nextTicket = (Ticket) pack.get(1);
            sendMessage = (boolean) pack.get(2);
            if (sendMessage) {
                if (getNbPropositions(receivedMessage.getEmitter()) == MAX_NB_PROPOSITIONS && nextAction == Action.PROPOSE) {
                    nextAction = Action.REFUSE;
                }
                Message nextMessage = new Message(receivedMessage.getMessageNumber() + 1, receivedMessage.getMessageNumber(),
                        nextAction, this, receivedMessage.getEmitter(), nextTicket);
                sendMessage(receivedMessage.getEmitter().getId(), nextMessage.toJSONString());
                incrementNbPropositions(receivedMessage.getEmitter());
            }
        } else {
            Message nextMessage = new Message(receivedMessage.getMessageNumber() + 1, receivedMessage.getMessageNumber(),
                    Action.REFUSE, this, receivedMessage.getEmitter(), receivedMessage.getTicket());
            sendMessage(receivedMessage.getEmitter().getId(), nextMessage.toJSONString());
        }
    }

    protected abstract ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage);

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
                '}';
    }

    int getNbPropositions(Agent agent) {
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
