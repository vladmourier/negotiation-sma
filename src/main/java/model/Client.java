package model;

import java.util.HashMap;

/**
 * Represents a customer able to negociate with ticket Suppliers
 */
public class Client extends Agent {
    HashMap<Flight, Integer> maxPricePerFlight;
    int wallet;

    public Client(String name, int wallet, int id) {
        super(name, id);
        this.name = name;
        this.wallet = wallet;
        maxPricePerFlight = new HashMap<Flight, Integer>();

        maxPricePerFlight.put(new Flight(Destination.LYON, Destination.PARIS), 80);
        maxPricePerFlight.put(new Flight(Destination.PARIS, Destination.LYON), 80);

    }
}
