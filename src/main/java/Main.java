import model.*;
import model.communication.message.Action;
import model.communication.message.Message;

import java.util.Date;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Client c = new Client("John doe", 1000, 0);

        Supplier s = new Supplier("John doe", 1);
        new Thread(c).start();
    }
}
