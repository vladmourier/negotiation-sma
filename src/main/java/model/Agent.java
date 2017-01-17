package model;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.events.MessageReceivedListener;
import model.communication.message.Action;
import model.communication.message.Message;
import model.negotiation.Negotiation;
import model.travel.Ticket;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the common attributes/behavior to
 */
public abstract class Agent implements Runnable, MessageReceivedListener {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static int lastAgentId = -1;

    /**
     * List of all agents ever created
     */
    public static HashMap<Integer, Agent> agents = new HashMap<Integer, Agent>();

    /**
     * Name of the Agent
     */
    String name;
    /**
     * ID of the agent
     */
    int Id;
    /**
     * Last best offered price
     */
    protected HashMap<Agent, Ticket> lastBestOffer = new HashMap<>();

    protected ArrayList<Negotiation> negotiations = new ArrayList<>();
    ArrayList<Agent> blackListedAgents = new ArrayList<>();

    protected Double lowThreshold;
    protected Double highThreshold;
    Negotiation currentNegotiation;

    /**
     * Agent's communication module
     */
    AgentSocket agentSocket;

    /**
     * Constructor
     *
     * @param name
     * @param lowThreshold
     * @param highThreshold
     */
    public Agent(String name, double lowThreshold, double highThreshold) {
        this.name = name;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        Id = lastAgentId + 1;
        agents.put(Id, this);

        lastAgentId++;
    }


    /**
     * Sends a message to the Agent designated by the provided id
     *
     * @param recipient
     * @param msg
     */
    public void sendMessage(int recipient, Message msg) {
        if (recipient != this.getId() && agents.containsKey(recipient)) {
            this.agentSocket.sendMessage(recipient, msg.toJSONString());
            currentNegotiation.setLastOfferedTicket(msg.getTicket());
        }
    }


    public boolean addNegotiation(Negotiation negotiation) {
        return this.negotiations.add(negotiation);
    }

    public boolean blackList(Agent agent) {
        return this.blackListedAgents.add(agent);
    }

    public boolean isblackListed(Agent agent) {
        return this.blackListedAgents.contains(agent);
    }

    /**
     * Returns true if the submitted offer is the best the agent received from his interlocutor
     *
     * @param agent
     * @param ticket
     * @return
     */
    protected abstract boolean isBestOffer(Agent agent, Ticket ticket);

    /**
     * Sets the provided offer as the last best offer received from the interlocutor
     *
     * @param agent
     * @param ticket
     */
    protected void setLastBestOffer(Agent agent, Ticket ticket) {
        this.lastBestOffer.put(agent, ticket);
    }

    /**
     * Returns the last best offer provided by the supplied interlocutor
     *
     * @param agent
     * @return
     */
    protected Ticket getLastBestOffer(Agent agent) {
        return this.lastBestOffer.get(agent);
    }

    /**
     * Returns true if the tickets' price is around the extreme value the agent can support (upper and lower thresholds apply here)
     *
     * @param ticket
     * @return
     */
    protected abstract boolean isCorrectDeal(Ticket ticket);

    /**
     * returns true if the ticket's price is a great deal according to the thresholds
     *
     * @param ticket
     * @return
     */
    protected abstract boolean isGreatDeal(Ticket ticket);

    /**
     * Returns true if the ticket's prie is a very bad deal according to the thresholds
     *
     * @param ticket
     * @return
     */
    protected abstract boolean isPoorDeal(Ticket ticket);

    /**
     * Listener of the MessageReceivedEvents
     * Contains most of the shared logic regarding negotiation
     *
     * @param event
     */
    public void messageReceived(MessageReceivedEvent event) {
        Message receivedMessage = event.getSource().getLastReceivedMessage();
        boolean sendMessage = true;
        Agent emitter = receivedMessage.getEmitter();
        Ticket proposedTicket = receivedMessage.getTicket();
        Ticket nextTicket = new Ticket(proposedTicket.getFlight(), proposedTicket.getPrice(), proposedTicket.getDate());
        Action nextAction = Action.ORDER;

        currentNegotiation = Negotiation.createOrFindNegotiation(this.getClass() == Client.class ? (Client) this : (Client) emitter, proposedTicket.getFlight());
        currentNegotiation.setLastOfferedTicket(proposedTicket);

        System.out.println(receivedMessage);

        if (receivedMessage.isWellFormed() && !isblackListed(emitter) && !currentNegotiation.isDone() && currentNegotiation.isNotDoneYetWithAgent(emitter) && specialChecks(receivedMessage)) {
            ArrayList<Object> pack = treatMessageAccordingToAction(receivedMessage, nextAction, proposedTicket, nextTicket, sendMessage);
            nextAction = (Action) pack.get(0);
            nextTicket = (Ticket) pack.get(1);
            sendMessage = (boolean) pack.get(2);
            if (sendMessage) {
                if (currentNegotiation.isDoneWithAgent(emitter) && nextAction == Action.PROPOSE) {
                    nextAction = Action.REFUSE;
                }
                Message nextMessage = new Message(receivedMessage.getMessageNumber(),
                        nextAction, this, emitter, nextTicket);
                sendMessage(emitter.getId(), nextMessage);
                currentNegotiation.incrementNbPropositions(emitter);
            }
        } else {
            if (shouldRefuse(receivedMessage.getAction())) {
                Message nextMessage = new Message(receivedMessage.getMessageNumber(),
                        Action.REFUSE, this, emitter, receivedMessage.getTicket());
                sendMessage(emitter.getId(), nextMessage);

            }
        }
    }

    /**
     * Returns true if the Agent must send a REFUSE message
     *
     * @param action
     * @return
     */
    private boolean shouldRefuse(Action action) {
        return (!currentNegotiation.isDone() && action != Action.REFUSE)//On ne répond à tout sauf à un refuse
                || (currentNegotiation.isDone() && action == Action.PROPOSE); //Si on a déjà accepté on refuse les suivants
    }

    /**
     * Performs checks specific to the buyer or the seller
     *
     * @param received
     * @return
     */
    protected abstract boolean specialChecks(Message received);

    /**
     * Contains the buyer/Seller logic of negotiation
     *
     * @param receivedMessage
     * @param nextAction
     * @param proposedTicket
     * @param nextTicket
     * @param sendMessage
     * @return An arrayList containing nextAction, nextTicket and a boolean indicating whether or not the agent will have to send a message
     */
    protected abstract ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage);

    /**
     * Returns the agent name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the agent name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the agent's id
     *
     * @return
     */
    public int getId() {
        return Id;
    }

    /**
     * Sets the agent id
     *
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
}
