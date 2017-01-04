package model.communication.message;

import model.Agent;
import model.Destination;
import model.Flight;
import model.Ticket;
import org.json.*;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Message implements JSONString {

    int messageNumber;
    int previousMessageNumber;
    Action action;
    Agent emitter;
    Agent recipient;
    Ticket ticket;


    public Message(String s) {
        JSONObject jsonObject = new JSONObject(s);
        this.messageNumber = jsonObject.getInt("messageNumber");
        this.previousMessageNumber = jsonObject.getInt("previousMessageNumber");
        this.action = Action.valueOf(jsonObject.getString("action"));
        this.emitter    = Agent.agents.get(jsonObject.getInt("emitter"));
        this.recipient  = Agent.agents.get(jsonObject.getInt("recipient"));
        this.ticket     = Ticket.tickets.get(jsonObject.getInt("ticket"));
    }

    public Message(int messageNumber, int previousMessageNumber, Action action, Agent emitter, Agent recipient, Ticket ticket) {
        this.messageNumber = messageNumber;
        this.previousMessageNumber = previousMessageNumber;
        this.action = action;
        this.emitter = emitter;
        this.recipient = recipient;
        this.ticket = ticket;
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
}
