import model.Client;
import model.Supplier;

public class Main {
    public static void main(String[] args) {
        Client c = new Client("BUYER", 1000, 0, 0.8, 1.2);

        Supplier s = new Supplier("SELLER", 1, 0.9, 1.5);
        new Thread(c).start();
    }
}
