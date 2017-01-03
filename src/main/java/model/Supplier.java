package model;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent {
    HashMap<Flight, Integer> minPricePerFlight;

    public Supplier(){
        minPricePerFlight = new HashMap<>();
    }

    public Supplier(String name) {
        this.name = name;
    }
}
