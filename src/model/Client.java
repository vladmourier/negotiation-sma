package model;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Client {
    HashMap<Flight, Integer> maxPricePerFlight;
    String name;
    int wallet;

    public Client(){
        maxPricePerFlight = new HashMap<>();
    }

    public Client(String name, int wallet) {
        this();
        this.name = name;
        this.wallet = wallet;
    }
}
