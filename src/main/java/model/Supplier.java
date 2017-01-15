package model;

import model.communication.AgentSocket;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent {
    HashMap<Flight, Integer> minPricePerFlight;
    ArrayList<Ticket> offers;
    ArrayList<Ticket> sales = new ArrayList<>();


    public Supplier(String name, int id, double lowThreshold, double highThreshold) {
        super(name, id, lowThreshold, highThreshold);

        minPricePerFlight = new HashMap<>();
        minPricePerFlight.put(new Flight(Destination.PARIS, Destination.LYON), 100);
        minPricePerFlight.put(new Flight(Destination.LYON, Destination.PARIS), 200);
        this.agentSocket = new AgentSocket(Id);
        this.agentSocket.addMessageReceivedListener(this);
    }

    @Override
    protected boolean isBestOffer(Agent agent, Ticket ticket) {
        return this.lastBestOffer.get(agent) == null || this.lastBestOffer.get(agent).getPrice() - ticket.getPrice() < 0;
    }

    @Override
    protected boolean isCorrectDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / minPriceForTicket >= lowThreshold && ((double) ticketPrice) / minPriceForTicket <= highThreshold;
    }

    @Override
    protected boolean isPoorDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / minPriceForTicket < lowThreshold;
    }

    @Override
    protected boolean isGreatDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / minPriceForTicket > highThreshold;
    }

    public void run() {
        //Nothing to do here since suppliers are not launched as separate threads
    }

    @Override
    protected ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage) {
        ArrayList<Object> pack = new ArrayList<>();
        switch (receivedMessage.getAction()) {
            case CALL:
                //Todo : offer ticket
                nextAction = Action.PROPOSE;
            case ORDER:
                //Todo : negotiate new ticket
                nextAction = negotiate(receivedMessage.getEmitter(), proposedTicket, nextTicket);
                if (isBestOffer(receivedMessage.getEmitter(), proposedTicket)) {
                    setLastBestOffer(receivedMessage.getEmitter(), proposedTicket);
                } else {
                    if (getNbPropositions(receivedMessage.getEmitter()) >= 2 * MAX_NB_PROPOSITIONS / 3) {
                        Double nextPrice = getLastBestOffer(receivedMessage.getEmitter()).getPrice() * 1.3;
                        nextTicket.setPrice(nextPrice.intValue());
                    }
                }
                break;
            case PROPOSE:
                // TOdo : should not happen unless suppliers are clients too
                break;
            case ACCEPT:
                //Todo : Sell ticket
                sales.add(proposedTicket);
                nextAction = Action.ACCEPT;
                break;
            case REFUSE:
                //Todo : end communication
                break;
        }
        pack.add(nextAction);
        pack.add(nextTicket);
        pack.add(sendMessage);
        return pack;
    }

    public Action negotiate(Agent emitter, Ticket orderedTicket, Ticket nextTicket) {
        if (getNbPropositions(emitter) <= MAX_NB_PROPOSITIONS) {
            Double negotiatedPrice;
            if (isGreatDeal(orderedTicket)) {
                // accept
                return Action.ACCEPT;
            } else if (isCorrectDeal(orderedTicket)) {
                negotiatedPrice = minPricePerFlight.get(orderedTicket.getFlight()) -
                        (0.3 - ((double) orderedTicket.getPrice() / minPricePerFlight.get(orderedTicket.getFlight()))) * minPricePerFlight.get(orderedTicket.getFlight());
                nextTicket.setPrice(negotiatedPrice.intValue());
                return Action.PROPOSE;
                // propose
            } else if (isPoorDeal(orderedTicket)) {
                nextTicket.setPrice(minPricePerFlight.get(orderedTicket.getFlight()));
                return Action.PROPOSE;
                // propose
            } else {
                return Action.REFUSE;
            }
        } else {
            // refuse
            return Action.REFUSE;
        }
    }
}