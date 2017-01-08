package model;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Represents a customer able to negociate with ticket Suppliers
 */
public class Client extends Agent {
    HashMap<Flight, Integer> maxPricePerFlight;
    int wallet;

    public Client(String name, int wallet, int id) {
        super(name, id);
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

    public void messageReceived(MessageReceivedEvent event) {
        Message receivedMessage = event.getSource().getLastReceivedMessage();
        System.out.println(receivedMessage);
        if (receivedMessage.isWellFormed()) {
            Ticket proposedTicket = receivedMessage.getTicket();
            Ticket nextTicket = new Ticket(proposedTicket.getId() + 1, proposedTicket.getFlight(), proposedTicket.getPrice(), proposedTicket.getDate());
            Action nextAction = Action.ORDER;
            switch (receivedMessage.getAction()) {
                case PROPOSE:
                    //Todo : negociate
                    nextAction = negotiate(receivedMessage.getEmitter(), proposedTicket, nextTicket);
                    break;
                case CALL:
                case ORDER:
                case ACCEPT:
                case REFUSE:
                    //Todo : Should not happen
                    break;
            }
            Message nextMessage = new Message(receivedMessage.getMessageNumber() + 1, receivedMessage.getMessageNumber(),
                    nextAction, this, receivedMessage.getEmitter(), nextTicket);
            sendMessage(receivedMessage.getEmitter().getId(), nextMessage.toJSONString());
        } else {

        }
    }

    public Action negotiate(Agent emitter, Ticket submittedTicket, Ticket nextTicket) {
        if (getNbPropositions(emitter) <= MAX_NB_PROPOSITIONS) {
            Double b;
            if (submittedTicket.getPrice() > maxPricePerFlight.get(submittedTicket.getFlight())) {
                b = maxPricePerFlight.get(submittedTicket.getFlight()) * 0.8;
                nextTicket.setPrice(b.intValue());
                incrementNbPropositions(emitter);
                //propose
                return Action.PROPOSE;
            } else if (submittedTicket.getPrice() <= maxPricePerFlight.get(submittedTicket.getFlight()) * 0.25) {
                //accept
                return Action.ACCEPT;
            } else {
                b = submittedTicket.getPrice() * 1.1;
                nextTicket.setPrice(b.intValue());
                incrementNbPropositions(emitter);
                //propose
                return Action.PROPOSE;
            }
        } else {
            // refuse
            return Action.REFUSE;
        }
    }
}
