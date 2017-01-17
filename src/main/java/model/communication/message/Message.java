package model.communication.message;

import model.Agent;
import model.travel.Ticket;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Message implements JSONString {

    static int lastMessageNumber = -1;

    /**
     * Current message's number
     */
    private int messageNumber;
    /**
     * Message number this is a response to. -1 if this is the initial message
     */
    private int previousMessageNumber;
    /**
     * Action : protocol word regarding tickets negotiation
     */
    private Action action;
    /**
     * The Agent sending the message
     */
    private Agent emitter;
    /**
     * The Recipient of the message
     */
    private Agent recipient;
    /**
     * The offered or asked ticket
     */
    private Ticket ticket;


    /**
     * Creates the message from a JSON String (serialized message)
     *
     * @param s
     */
    public Message(String s) {
        JSONObject jsonObject = new JSONObject(s);
        this.messageNumber = jsonObject.getInt("messageNumber");
        this.previousMessageNumber = jsonObject.getInt("previousMessageNumber");
        this.action = Action.valueOf(jsonObject.getString("action"));
        this.emitter = Agent.agents.get(jsonObject.getInt("emitter"));
        this.recipient = Agent.agents.get(jsonObject.getInt("recipient"));
        this.ticket = Ticket.tickets.get(jsonObject.getInt("ticket"));
    }

    /**
     * Creates a message
     *
     * @param previousMessageNumber
     * @param action
     * @param emitter
     * @param recipient
     * @param ticket
     */
    public Message(int previousMessageNumber, Action action, Agent emitter, Agent recipient, Ticket ticket) {
        this.messageNumber = lastMessageNumber+1;
        this.previousMessageNumber = previousMessageNumber;
        this.action = action;
        this.emitter = emitter;
        this.recipient = recipient;
        this.ticket = ticket;
        lastMessageNumber++;
    }

    /**
     * Serializes the message into a JSON String
     *
     * @return
     */
    public String toJSONString() {
        return "{" +
                "messageNumber:" + messageNumber +
                ", previousMessageNumber:" + previousMessageNumber +
                ", action:" + action +
                ", emitter:" + emitter.getId() +
                ", recipient:" + recipient.getId() +
                ", ticket:" + ticket.getId() +
                '}';
    }

    /**
     * Returns true if the message has a correct syntax
     * @return whether the message syntax is correct or not
     */
    public boolean isWellFormed() {
        boolean isWellFormed = Action.getAll().contains(action);
        isWellFormed = isWellFormed && (messageNumber >= 0);
        if (action == Action.CALL)
            isWellFormed = isWellFormed && (previousMessageNumber == -1);
        isWellFormed = isWellFormed && (emitter != null && Agent.agents.containsKey(emitter.getId()));
        isWellFormed = isWellFormed && (recipient != null && Agent.agents.containsKey(recipient.getId()));


        return isWellFormed;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getPreviousMessageNumber() {
        return previousMessageNumber;
    }

    public void setPreviousMessageNumber(int previousMessageNumber) {
        this.previousMessageNumber = previousMessageNumber;
    }

    public Agent getEmitter() {
        return emitter;
    }

    public void setEmitter(Agent emitter) {
        this.emitter = emitter;
    }

    public Agent getRecipient() {
        return recipient;
    }

    public void setRecipient(Agent recipient) {
        this.recipient = recipient;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageNumber=" + messageNumber +
                ", previousMessageNumber=" + previousMessageNumber +
                ", action=" + action +
                ", emitter=" + emitter +
                ", recipient=" + recipient +
                ", ticket=" + ticket +
                '}';
    }


}
