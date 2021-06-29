import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    ServerSocket server;
    int serverPort = 123;
    public Server() {
        try {
            server = new ServerSocket(serverPort);
            System.out.println("Server Created!");
            while (true) {
                Socket client = server.accept();
                System.out.println("Connected to New Client!");
                Thread t = new Thread(new ClientManager(client));
                t.start();
            }
        } catch (IOException e) {}
    }
    public static void main(String[] args) {
        new Server();
    }
}