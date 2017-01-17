package model.negotiation;

import model.Agent;
import model.Client;
import model.travel.Flight;
import model.Supplier;
import model.travel.Ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vlad on 17/01/2017.
 */
public class Negotiation {

    static ArrayList<Negotiation> negotiations = new ArrayList<>();
    /**
     * Max number of studied propositions per negotiation dialogue (ie max number of message per agent per dialogue)
     */
    public static final int MAX_NB_PROPOSITIONS = 6;
    /**
     * Number of propositions made by the Agent identified by its iD
     */
    protected HashMap<Integer, Integer> nbPropositions = new HashMap<>();

    /**
     * The client who initiated the negotiation
     */
    Client origin;

    /**
     * Suppliers implicated in the negociation
     */
    ArrayList<Supplier> suppliers;
    /**
     * Flight the client is negotiating for
     */
    Flight flight;
    /**
     * Start of the negotiation
     */
    Date dateStart;
    /**
     * End of the negotiation
     */
    Date dateEnd;

    /**
     * Last ticket engaged in the negotiation
     */
    Ticket lastOfferedTicket;

    public Negotiation(Client origin, Flight flight) {
        this.suppliers = new ArrayList<>();
        this.flight = flight;
        origin.addNegotiation(this);
        this.origin = origin;
        this.dateStart = new Date();
        negotiations.add(this);
    }

    /**
     * Sets the dateEnd
     */
    public long endNegotiation() {
        dateEnd = new Date();
        return getNegotiationDuration();
    }

    public long getNegotiationDuration() {
        return (dateEnd.getTime() - dateStart.getTime()) / 1000;
    }

    public static Negotiation createOrFindNegotiation(Client origin, Flight flight) {
        int i = 0;
        for(Negotiation n : negotiations){
            if(n == null) return createOrFindNegotiation(origin, flight);//Empêche NullPointerException
            i++;
            if(n.origin.equals(origin) && n.flight.equals(flight))
                return n;
        }
        return new Negotiation(origin,flight);
    }


    /**
     * Returns true if the negotiation is done (ie the client or a supplier accepted a ticket)
     * @return whether or not a deal has been closed
     */
    public boolean isDone(){
        return dateEnd != null;
    }

    /**
     * Returns true if the Agent has made
     * @param agent
     * @return
     */
    public boolean isDoneWithAgent(Agent agent){
        return getNbPropositions(agent) >= MAX_NB_PROPOSITIONS;
    }

    /**
     * Returns true if the agent can still make a proposition
     * @param agent
     * @return
     */
    public boolean isNotDoneYetWithAgent(Agent agent){
        return getNbPropositions(agent) <= MAX_NB_PROPOSITIONS;
    }


    /////////////////////////////////////////////////////////////////////
    // Getters & Setters
    /////////////////////////////////////////////////////////////////////

    /**
     * Returns the number of propositions the provided agents already made
     *
     * @param agent
     * @return
     */
    public int getNbPropositions(Agent agent) {
        if (!nbPropositions.containsKey(agent.getId()))
            nbPropositions.put(agent.getId(), 0);
        return nbPropositions.get(agent.getId());
    }

    /**
     * Increments by one the number of propositions the provided agent made
     *
     * @param agent
     */
    public void incrementNbPropositions(Agent agent) {
        this.nbPropositions.put(agent.getId(), this.nbPropositions.get(agent.getId()) + 1);
    }

    /**
     * Sets the number of propositions the supplied agent have made
     *
     * @param agent
     * @param nbPropositions
     */
    public void setNbPropositions(Agent agent, int nbPropositions) {
        this.nbPropositions.put(agent.getId(), nbPropositions);
    }

    public Flight getFlight() {
        return flight;
    }

    public Ticket getLastOfferedTicket() {
        return lastOfferedTicket;
    }

    public void setLastOfferedTicket(Ticket lastOfferedTicket) {
        this.lastOfferedTicket = lastOfferedTicket;
    }

    public boolean addSupplier(Supplier s){
        if(suppliers == null) suppliers = new ArrayList<>();
        return this.suppliers.add(s);
    }

    public boolean removeSupplier(Supplier s){
        return suppliers.remove(s);
    }
}
