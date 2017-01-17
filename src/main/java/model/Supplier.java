package model;

import model.communication.AgentSocket;
import model.communication.message.Action;
import model.communication.message.Message;
import model.negotiation.Negotiation;
import model.travel.Destination;
import model.travel.Flight;
import model.travel.Ticket;
import model.travel.UntrackedFlight;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent {
    /**
     * The minimum price the supplier is willing to earn per flight
     */
    HashMap<UntrackedFlight, Integer> minPricePerFlight;
    HashMap<UntrackedFlight, Integer> availableFlights = new HashMap<>();
    /**
     * Array of tickets sold by the seller to clients who accepted them
     */
    ArrayList<Ticket> sales = new ArrayList<>();


    public Supplier(String name, double lowThreshold, double highThreshold) {
        super(name, lowThreshold, highThreshold);

        minPricePerFlight = new HashMap<>();
        this.agentSocket = new AgentSocket(Id);
        this.agentSocket.addMessageReceivedListener(this);
    }

    public void addAvailableFlight(UntrackedFlight flight,int quantity, int minPricePerFlight){
        availableFlights.put(flight, quantity);
        this.minPricePerFlight.put(flight, minPricePerFlight);
    }

    @Override
    protected boolean isBestOffer(Agent agent, Ticket ticket) {
        return this.lastBestOffer.get(agent) == null || this.lastBestOffer.get(agent).getPrice() - ticket.getPrice() < 0;
    }

    @Override
    protected boolean isCorrectDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / minPriceForTicket >= lowThreshold && ((double) ticketPrice) / minPriceForTicket <= highThreshold;
    }

    @Override
    protected boolean isPoorDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / minPriceForTicket < lowThreshold;
    }

    @Override
    protected boolean specialChecks(Message message) {
        Integer amount = availableFlights.get(currentNegotiation.getFlight().getUntrackedFlight());
        return amount != null && amount>0;
    }

    @Override
    protected boolean isGreatDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int minPriceForTicket = minPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / minPriceForTicket > highThreshold;
    }

    @Override
    protected ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage) {
        ArrayList<Object> pack = new ArrayList<>();
        Agent emitter = receivedMessage.getEmitter();
        switch (receivedMessage.getAction()) {
            case CALL:
            case ORDER:
                nextAction = negotiate(emitter, proposedTicket, nextTicket);
                if (isBestOffer(emitter, proposedTicket)) {
                    setLastBestOffer(emitter, proposedTicket);
                } else {
                    if (currentNegotiation.getNbPropositions(emitter) >= 2 * Negotiation.MAX_NB_PROPOSITIONS / 3) {
                        Double nextPrice = getLastBestOffer(emitter).getPrice() * highThreshold;
                        nextTicket.setPrice(nextPrice.intValue());
                    }
                }
                break;
            case PROPOSE:
                //Should not happen unless suppliers are clients too
                nextAction = Action.ERROR;
                break;
            case ACCEPT:
                //Sell ticket
                sales.add(proposedTicket);
                nextAction = Action.ACCEPT;
                currentNegotiation.endNegotiation();
                break;
            case REFUSE:
                //end communication
                sendMessage = false;
                break;
        }
        pack.add(nextAction);
        pack.add(nextTicket);
        pack.add(sendMessage);
        return pack;
    }

    public Action negotiate(Agent emitter, Ticket orderedTicket, Ticket nextTicket) {
        if (currentNegotiation.isNotDoneYetWithAgent(emitter)) {
            Double negotiatedPrice;
            if (isGreatDeal(orderedTicket)) {
                // accept
                return Action.ACCEPT;
            } else if (isCorrectDeal(orderedTicket)) {
                negotiatedPrice = minPricePerFlight.get(orderedTicket.getFlight().getUntrackedFlight()) -
                        (0.3 - ((double) orderedTicket.getPrice() / minPricePerFlight.get(orderedTicket.getFlight().getUntrackedFlight()))) * minPricePerFlight.get(orderedTicket.getFlight().getUntrackedFlight());
                nextTicket.setPrice(negotiatedPrice.intValue());
                return Action.PROPOSE;
                // propose
            } else if (isPoorDeal(orderedTicket)) {
                nextTicket.setPrice(minPricePerFlight.get(orderedTicket.getFlight().getUntrackedFlight()));
                minPricePerFlight.put(orderedTicket.getFlight().getUntrackedFlight(), ((int)(nextTicket.getPrice() * lowThreshold)));
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

    @Override
    public void run() {
        //Empty body since suppliers are not launched as separate threads
    }
}