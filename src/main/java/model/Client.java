package model;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Client extends Agent {
    HashMap<Flight, Integer> maxPricePerFlight;
    int wallet;

    public Client(){
        super();
        maxPricePerFlight = new HashMap<>();
    }

    public Client(String name, int wallet) {
        this();
        this.name = name;
        this.wallet = wallet;
    }
}
