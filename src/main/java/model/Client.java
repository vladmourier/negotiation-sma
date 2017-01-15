package model;

import model.communication.AgentSocket;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Represents a customer able to negociate with ticket Suppliers
 */
public class Client extends Agent {
    HashMap<Flight, Integer> maxPricePerFlight;
    ArrayList<Ticket> boughtTickets = new ArrayList<>();
    int wallet;

    public Client(String name, int wallet, int id, double lowThreshold, double highThreshold) {
        super(name, id, lowThreshold, highThreshold);
        maxPricePerFlight = new HashMap<>();
        this.agentSocket = new AgentSocket(Id);
        this.wallet = wallet;
        this.agentSocket.addMessageReceivedListener(this);

        maxPricePerFlight.put(new Flight(Destination.LYON, Destination.PARIS), 80);
        maxPricePerFlight.put(new Flight(Destination.PARIS, Destination.LYON), 80);
    }

    public void run() {
        for (Agent agent : Agent.agents.values()) {
            if (agent.getClass() == Supplier.class) {
                Ticket ticket = new Ticket(0, Destination.LYON, Destination.PARIS, 40, new GregorianCalendar(2017, GregorianCalendar.FEBRUARY, 5).getTime());
                Message m = new Message(0, -1, Action.CALL, this, agent, ticket);
                sendMessage(agent.getId(), m.toJSONString());
            }
        }
    }

    protected ArrayList<Object> treatMessageAccordingToAction(Message receivedMessage, Action nextAction, Ticket proposedTicket, Ticket nextTicket, Boolean sendMessage) {
        ArrayList<Object> pack = new ArrayList<>();
        switch (receivedMessage.getAction()) {
            case PROPOSE:
                nextAction = negotiate(receivedMessage.getEmitter(), proposedTicket, nextTicket);
                if (isBestOffer(receivedMessage.getEmitter(), proposedTicket)) {
                    setLastBestOffer(receivedMessage.getEmitter(), proposedTicket);
                } else {
                    if (getNbPropositions(receivedMessage.getEmitter()) >= 2 * MAX_NB_PROPOSITIONS / 3) {
                        Double nextPrice = getLastBestOffer(receivedMessage.getEmitter()).getPrice() * 0.8;
                        nextTicket.setPrice(nextPrice.intValue());
                    }
                }
                break;
            case ACCEPT:
                sendMessage = false;
                boughtTickets.add(proposedTicket);
                break;
            case REFUSE:
                sendMessage = false;
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
        if (getNbPropositions(emitter) <= MAX_NB_PROPOSITIONS) {
            Double b;
            if (isGreatDeal(submittedTicket)) {
                //accept
                return Action.ACCEPT;
            } else if (isCorrectDeal(submittedTicket)) {
                b = submittedTicket.getPrice() * 0.8;
                nextTicket.setPrice(b.intValue());
                return Action.ORDER;
            } else if (isPoorDeal(submittedTicket)) {
                nextTicket.setPrice(maxPricePerFlight.get(submittedTicket.getFlight()));
                return Action.ORDER;
            } else {
                return Action.REFUSE;
            }
        } else {
            // refuse
            return Action.REFUSE;
        }
    }

    @Override
    protected boolean isBestOffer(Agent agent, Ticket ticket) {
        return this.lastBestOffer.get(agent) == null || this.lastBestOffer.get(agent).getPrice() - ticket.getPrice() > 0;
    }

    @Override
    protected boolean isCorrectDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / maxPriceforTicket >= lowThreshold && ((double) ticketPrice) / maxPriceforTicket <= highThreshold;
    }

    @Override
    protected boolean isGreatDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / maxPriceforTicket < lowThreshold;
    }

    @Override
    protected boolean isPoorDeal(Ticket ticket) {
        int ticketPrice = ticket.getPrice();
        int maxPriceforTicket = maxPricePerFlight.get(ticket.getFlight());
        return ((double) ticketPrice) / maxPriceforTicket > highThreshold;
    }
}
