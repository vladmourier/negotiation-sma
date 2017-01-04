package model;

import java.util.HashMap;

/**
 * Created by Vlad on 03/01/2017.
 */
public class Supplier extends Agent {
    HashMap<Flight, Integer> minPricePerFlight;


    public Supplier(String name, int id) {
        super(name, id);
        minPricePerFlight = new HashMap<Flight, Integer>();
    }
}
