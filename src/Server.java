import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server
 * <p>
 * Starts the server and hands each client connection to a ServerThread
 *
 * @author Andrew Lu, Maanas Karwa
 * @version December 12, 2022
 */
public class Server {
    public static void main(String[] args) throws IOException {
        //initialize all possible data into classes
        Person.initialize(false);
        Product.initializeBasic(false);
        Seller.initializeBasic(false);
        Customer.initializeBasic(false);
        Store.initializeBasic(false);
        Market.initializeBasic();
        Sale.initializeBasic(false);

        Product.initializeAdvanced();
        Sale.initializeAdvanced();
        Store.initializeAdvanced();
        Seller.initializeAdvanced();
        Customer.initializeAdvanced();

        ServerSocket serverSocket = new ServerSocket(4242);
        System.out.printf("waiting for connections on %s\n", serverSocket);

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ServerThread thread = new ServerThread(socket);
                new Thread(thread).start();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
