package model;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.events.MessageReceivedListener;

import java.util.HashMap;

/**
 * Represents a customer able to negociate with ticket Suppliers
 */
public class Client extends Agent implements Runnable, MessageReceivedListener {
    HashMap<Flight, Integer> maxPricePerFlight;
    int wallet;
    AgentSocket agentSocket;

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

    }

    public void sendMessage(int recipient, String msg) {
        if (recipient != this.getId() && recipient > 0) {
            this.agentSocket.sendMessage(recipient, msg);
        }
    }

    public void messageReceived(MessageReceivedEvent event) {

    }

    public void negotiate(Ticket t) {
        if (getNbPropositions() <= 6) {
            Double b;
            if(t.getPrice()>maxPricePerFlight.get(t.getF())) {
                b = maxPricePerFlight.get(t.getF())*0.8;
                t.setPrice(b.intValue());
                setNbPropositions(getNbPropositions()+1);
                //propose
            } else if (t.getPrice() <= maxPricePerFlight.get(t.getF())*0.25) {
                //accept
            } else {
                b = t.getPrice()*1.1;
                t.setPrice(b.intValue());
                setNbPropositions(getNbPropositions()+1);
                //propose
            }
        } else {
            // refuse
        }
    }
}
