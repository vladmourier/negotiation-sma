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
import java.util.Date;
import java.util.HashMap;

/**
 * Represents a customer able to negociate with ticket Suppliers
 */
public class Client extends Agent {
    /**
     * Contains the maximum prices the client is ready to pay for flights
     */
    HashMap<UntrackedFlight, Integer> maxPricePerFlight;
    /**
     * Array containing tickets accepted by both the buyer and the seller
     */
    ArrayList<Ticket> boughtTickets = new ArrayList<>();
    /**
     * The total money of the client
     */
    int wallet;
    ArrayList<Ticket> wantedTickets = new ArrayList<>();
    int dateFlexibility;

    public Client(String name, int wallet, double lowThreshold, double highThreshold, int dateFlexibility) {
        super(name, lowThreshold, highThreshold);
        maxPricePerFlight = new HashMap<>();
        this.agentSocket = new AgentSocket(Id);
        this.wallet = wallet;
        this.agentSocket.addMessageReceivedListener(this);
        this.dateFlexibility = dateFlexibility;
    }

    public void setMaxPricePerFlight(UntrackedFlight flight, int maxPrice){
        maxPricePerFlight.put(flight, maxPrice);
    }

    public void purchase(){
        for(Ticket wantedTicket : wantedTickets){
            Negotiation n = new Negotiation(this, wantedTicket.getFlight());
            for (Agent agent : Agent.agents.values()) {
                currentNegotiation = n;
                if (agent.getClass() == Supplier.class)
                {
                    n.addSupplier((Supplier) agent);
                    n.setLastOfferedTicket(wantedTicket);
                    Message m = new Message(-1, Action.CALL, this, agent, wantedTicket);
                    sendMessage(agent.getId(), m);
                }
            }
        }
    }

    public boolean addWantedTicket(Ticket ticket){
        if(wantedTickets == null) wantedTickets = new ArrayList<>();
        return wantedTickets.add(ticket);
    }

    protected ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage) {
        ArrayList<Object> pack = new ArrayList<>();
        Agent emitter = receivedMessage.getEmitter();
        switch (receivedMessage.getAction()) {
            case PROPOSE:
                nextAction = negotiate(emitter, proposedTicket, nextTicket);
                if (isBestOffer(emitter, proposedTicket)) {
                    setLastBestOffer(emitter, proposedTicket);
                } else {
                    if (currentNegotiation.getNbPropositions(emitter) >= 2 * Negotiation.MAX_NB_PROPOSITIONS / 3) {
                        Double nextPrice = getLastBestOffer(emitter).getPrice() * 0.8;
                        nextTicket.setPrice(nextPrice.intValue());
                    }
                }
                break;
            case ACCEPT:
                sendMessage = false;
                boughtTickets.add(proposedTicket);
                currentNegotiation.endNegotiation();
                System.out.println(ANSI_BLUE + "Ticket bought" + ANSI_RESET);
                break;
            case REFUSE:
                sendMessage = false;
                currentNegotiation.removeSupplier((Supplier) emitter);
                break;
            case CALL:
            case ORDER:
                nextAction = Action.ERROR;
                break;
        }
        pack.add(nextAction);
        pack.add(nextTicket);
        pack.add(sendMessage);
        return pack;
    }


    public Action negotiate(Agent emitter, Ticket submittedTicket, Ticket nextTicket) {
        if (currentNegotiation.isNotDoneYetWithAgent(emitter)) {
            Double b;
            if(!checkSubmittedTicketDates(submittedTicket)) {
                return Action.REFUSE;
            }
            if (isGreatDeal(submittedTicket) || isAcceptableDeal(emitter, submittedTicket)) {
                //accept
                System.out.println(ANSI_BLUE + "Ticket bought" + ANSI_RESET);
                System.out.println(new Date(99, 2, 17).compareTo(new Date(99,2,18)));
                return Action.ACCEPT;
            } else if (isCorrectDeal(submittedTicket)) {
                b = submittedTicket.getPrice() * 0.8;
                nextTicket.setPrice(b.intValue());
                return Action.ORDER;
            } else if (isPoorDeal(submittedTicket)) {
                nextTicket.setPrice(maxPricePerFlight.get(submittedTicket.getFlight().getUntrackedFlight()));
                maxPricePerFlight.put(submittedTicket.getFlight().getUntrackedFlight(), ((int) (nextTicket.getPrice() * highThreshold)));
                return Action.ORDER;
            } else {
                currentNegotiation.removeSupplier((Supplier)emitter);
                return Action.REFUSE;
            }
        } else {
            // refuse
            return Action.REFUSE;
        }
    }

    private boolean checkSubmittedTicketDates(Ticket submittedTicket) {
        for (Ticket t : wantedTickets) {
            if(getDays(t.getDate(), submittedTicket.getDate()) <= dateFlexibility && t.getFlight().equals(submittedTicket.getFlight())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAcceptableDeal(Agent emitter, Ticket submittedTicket){
        return (currentNegotiation.getNbPropositions(emitter) >= Negotiation.MAX_NB_PROPOSITIONS - 1 &&
                (submittedTicket.getPrice() / maxPricePerFlight.get(submittedTicket.getFlight().getUntrackedFlight())) > (4 * lowThreshold / 5));
    }

    @Override
    protected boolean isBestOffer(Agent agent, Ticket ticket) {
        return this.lastBestOffer.get(agent) == null || this.lastBestOffer.get(agent).getPrice() - ticket.getPrice() > 0;
    }

    @Override
    protected boolean isCorrectDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / maxPriceforTicket >= lowThreshold && ((double) ticketPrice) / maxPriceforTicket <= highThreshold;
    }

    @Override
    protected boolean isGreatDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / maxPriceforTicket < lowThreshold;
    }

    @Override
    protected boolean isPoorDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight().getUntrackedFlight());
        return ((double) ticketPrice) / maxPriceforTicket > highThreshold;
    }

    @Override
    protected boolean specialChecks(Message message) {
        //Makes sure the client can afford the ticket
        return currentNegotiation.getNbPropositions(message.getEmitter()) <= Negotiation.MAX_NB_PROPOSITIONS - 2 || currentNegotiation.getLastOfferedTicket().getPrice() <= wallet;
    }

    @Override
    public void run() {
        purchase();
    }
}
