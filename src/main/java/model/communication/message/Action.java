package model.communication.message;

import java.util.ArrayList;

/**
 * Created by Vlad on 03/01/2017.
 */
public enum Action {
    /**
     * Client Asking for a ticket
     */
    CALL,
    /**
     * Client orders a negotiated ticket
     */
    ORDER,
    /**
     * Supplier Offers a ticket as a response to call or order
     */
    PROPOSE,
    /**
     * Client Accepting the attached ticket
     */
    ACCEPT,
    /**
     * Client or Supplier refuses the deal
     */
    REFUSE,
    /**
     * Sent when a malformed message is received
     */
    ERROR;

    static ArrayList<Action> getAll(){
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(CALL);
        actions.add(ORDER);
        actions.add(PROPOSE);
        actions.add(ACCEPT);
        actions.add(REFUSE);
        return actions;
    }
}
