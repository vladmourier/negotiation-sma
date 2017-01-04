package model;

import java.util.ArrayList;
import java.util.HashMap;

import model.communication.AgentSocket;
import model.communication.events.MessageReceivedEvent;
import model.communication.events.MessageReceivedListener;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent implements Runnable, MessageReceivedListener {
    HashMap<Flight, Integer> minPricePerFlight;
    ArrayList<Ticket> offers;
    AgentSocket agentSocket;


    public Supplier(String name, int id) {
        super(name, id);

        minPricePerFlight = new HashMap<Flight, Integer>();
        minPricePerFlight.put(new Flight(Destination.PARIS, Destination.LYON), 100);
        minPricePerFlight.put(new Flight(Destination.LYON, Destination.PARIS), 200);
        this.agentSocket = new AgentSocket(Id);
        this.agentSocket.addMessageReceivedListener(this);
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
            if (t.getPrice() < minPricePerFlight.get(t.getF())) {
                b = minPricePerFlight.get(t.getF()) * 1.2;
                t.setPrice(b.intValue());
                setNbPropositions(getNbPropositions()+1);
                // propose
            } else if (t.getPrice() >= minPricePerFlight.get(t.getF()) * 2) {
                // accept
            } else {
                b = t.getPrice() * 0.9;
                t.setPrice(b.intValue());
                setNbPropositions(getNbPropositions()+1);
                // propose
            }
        } else {
            // refuse
        }
    }
}