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
    /**
     * Max number of studied propositions per negotiation dialogue (ie max number of message per agent per dialogue)
     */
    public static int MAX_NB_PROPOSITIONS = 6;

    /**
     * Name of the Agent
     */
    String name;
    /**
     * ID of the agent
     */
    int Id;
    /**
     * Number of propositions made by the Agent identified by its iD
     */
    protected HashMap<Integer, Integer> nbPropositions;
    /**
     * Last best offered price
     */
    protected HashMap<Agent, Ticket> lastBestOffer = new HashMap<>();

    protected Double lowThreshold;
    protected Double highThreshold;

    /**
     * Agent's communication module
     */
    AgentSocket agentSocket;

    /**
     * Costructor
     * @param name
     * @param id
     * @param lowThreshold
     * @param highThreshold
     */
    public Agent(String name, int id, double lowThreshold, double highThreshold) {
        this.name = name;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        Id = id;
        nbPropositions = new HashMap<>();
        agents.put(id, this);
    }


    /**
     * Sends a message to the Agent designated by the provided id
     * @param recipient
     * @param msg
     */
    public void sendMessage(int recipient, String msg) {
        if (recipient != this.getId() && agents.containsKey(recipient)) {
            this.agentSocket.sendMessage(recipient, msg);
        }
    }

    /**
     * Returns true if the submitted offer is the best the agent received from his interlocutor
     * @param agent
     * @param ticket
     * @return
     */
    protected abstract boolean isBestOffer(Agent agent, Ticket ticket);

    /**
     * Sets the provided offer as the last best offer received from the interlocutor
     * @param agent
     * @param ticket
     */
    protected void setLastBestOffer(Agent agent, Ticket ticket) {
        this.lastBestOffer.put(agent, ticket);
    }

    /**
     * Returns the last best offer provided by the supplied interlocutor
     * @param agent
     * @return
     */
    protected Ticket getLastBestOffer(Agent agent) {
        return this.lastBestOffer.get(agent);
    }

    /**
     * Returns true if the tickets' price is around the extreme value the agent can support (upper and lower thresholds apply here)
     * @param ticket
     * @return
     */
    protected abstract boolean isCorrectDeal(Ticket ticket);

    /**
     * returns true if the ticket's price is a great deal according to the thresholds
     * @param ticket
     * @return
     */
    protected abstract boolean isGreatDeal(Ticket ticket);

    /**
     * Returns true if the ticket's prie is a very bad deal according to the thresholds
     * @param ticket
     * @return
     */
    protected abstract boolean isPoorDeal(Ticket ticket);

    /**
     * Listener of the MessageReceivedEvents
     * Contains most of the shared logic regarding negotiation
     * @param event
     */
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

    /**
     * Contains the buyer/Seller logic of negotiation
     * @param receivedMessage
     * @param nextAction
     * @param proposedTicket
     * @param nextTicket
     * @param sendMessage
     * @return
     */
    protected abstract ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage);

    /**
     * Returns the agent name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the agent name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the agent's id
     * @return
     */
    public int getId() {
        return Id;
    }

    /**
     * Sets the agent id
     * @param id
     */
    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                '}';
    }

    /**
     * Returns the number of propositions the provided agents already made
     * @param agent
     * @return
     */
    int getNbPropositions(Agent agent) {
        if (!nbPropositions.containsKey(agent.getId()))
            nbPropositions.put(agent.getId(), 0);
        return nbPropositions.get(agent.getId());
    }

    /**
     * Sets the number of propositions the supplied agent have made
     * @param agent
     * @param nbPropositions
     */
    public void setNbPropositions(Agent agent, int nbPropositions) {
        this.nbPropositions.put(agent.getId(), nbPropositions);
    }

    /**
     * Increments by one the number of propositions the provided agent made
     * @param agent
     */
    public void incrementNbPropositions(Agent agent) {
        this.nbPropositions.put(agent.getId(), this.nbPropositions.get(agent.getId()) + 1);
    }
}
