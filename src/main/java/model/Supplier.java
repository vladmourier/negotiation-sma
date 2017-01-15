package model;

import java.util.ArrayList;
import java.util.HashMap;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.message.Action;
import model.communication.message.Message;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent {
    HashMap<Flight, Integer> minPricePerFlight;
    ArrayList<Ticket> offers;


    public Supplier(String name, int id) {
        super(name, id);

        minPricePerFlight = new HashMap<Flight, Integer>();
        minPricePerFlight.put(new Flight(Destination.PARIS, Destination.LYON), 100);
        minPricePerFlight.put(new Flight(Destination.LYON, Destination.PARIS), 200);
        this.agentSocket = new AgentSocket(Id);
        this.agentSocket.addMessageReceivedListener(this);
    }

    public void run() {
        //Nothing to do here since suppliers are not launched as separate threads
    }

    public void messageReceived(MessageReceivedEvent event) {
        Message receivedMessage = event.getSource().getLastReceivedMessage();
        System.out.println(receivedMessage);
        if(receivedMessage.isWellFormed() && getNbPropositions(receivedMessage.getEmitter()) <= MAX_NB_PROPOSITIONS){
            Ticket requestedTicket = receivedMessage.getTicket();
            Ticket nextTicket = new Ticket(requestedTicket.getId() + 1, requestedTicket.getFlight(), requestedTicket.getPrice(), requestedTicket.getDate());
            Action nextAction = Action.PROPOSE;
            switch (receivedMessage.getAction()) {
                case CALL:
                    //Todo : offer ticket
                    nextAction = Action.PROPOSE;
                case ORDER:
                    //Todo : negotiate new ticket
                    nextAction = negotiate(receivedMessage.getEmitter(), requestedTicket, nextTicket);
                    break;
                case PROPOSE:
                    // TOdo : should not happen unless suppliers are clients too
                    break;
                case ACCEPT:
                    //Todo : Sell ticket
                    break;
                case REFUSE:
                    //Todo : end communication
                    break;
            }
            Message nextMessage = new Message(receivedMessage.getMessageNumber() + 1, receivedMessage.getMessageNumber(),
                    nextAction, this, receivedMessage.getEmitter(), nextTicket);
            if (receivedMessage.getAction() != Action.REFUSE) {
                sendMessage(receivedMessage.getEmitter().getId(), nextMessage.toJSONString());
            } else {
                //Reset communication
                setNbPropositions(receivedMessage.getEmitter(), 0);
            }
        } else {

        }
    }

    public Action negotiate(Agent emitter, Ticket orderedTicket, Ticket nextTicket) {
        if (getNbPropositions(emitter) <= MAX_NB_PROPOSITIONS) {
            Double negotiatedPrice;
            if (orderedTicket.getPrice() < minPricePerFlight.get(orderedTicket.getFlight())) {
                negotiatedPrice = minPricePerFlight.get(orderedTicket.getFlight()) * 1.2;
                nextTicket.setPrice(negotiatedPrice.intValue());
                incrementNbPropositions(emitter);
                return Action.PROPOSE;
                // propose
            } else if (orderedTicket.getPrice() >= minPricePerFlight.get(orderedTicket.getFlight()) * 2) {
                // accept
                return Action.ACCEPT;
            } else {
                negotiatedPrice = orderedTicket.getPrice() * 0.9;
                nextTicket.setPrice(negotiatedPrice.intValue());
                incrementNbPropositions(emitter);
                return Action.PROPOSE;
                // propose
            }
        } else {
            // refuse
            return Action.REFUSE;
        }
    }
}