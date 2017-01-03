package model.communication.message;

import model.Agent;
import model.Flight;
import model.Ticket;
import org.json.*;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Message {

    int messageNumber;
    Action action;
    Agent emitter;
    Agent recipient;
    Ticket ticket;


    public Message(String s){

    }

    public Message(int messageNumber, Action action, Agent emitter, Agent recipient, Ticket ticket) {
        this.messageNumber = messageNumber;
        this.action = action;
        this.emitter = emitter;
        this.recipient = recipient;
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageNumber=" + messageNumber +
                ", action=" + action +
                ", emitter=" + emitter +
                ", recipient=" + recipient +
                ", ticket=" + ticket +
                '}';
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
}
