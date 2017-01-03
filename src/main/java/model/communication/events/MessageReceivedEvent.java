package model.communication.events;

import model.communication.AgentServer;

import java.util.EventObject;

/**
 * Created by Vlad on 17/12/2016.
 * Represents the events fired when the AgentServer receives a message
 */
public class MessageReceivedEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public MessageReceivedEvent(Object source) {
        super(source);
    }

    @Override
    public AgentServer getSource(){
        return (AgentServer) super.getSource();
    }
}
