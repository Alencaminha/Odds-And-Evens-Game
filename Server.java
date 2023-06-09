import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.print("Enter the port the server will bind to: ");
        final int PORT = new Scanner(System.in).nextInt();

        // Binding the server to the port
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("The server is now open!");

        // Opening server loop to connect and communicate with multiple clients
        while (!serverSocket.isClosed()) {
            // Listen for clients trying to connect to the server
            Socket socket = serverSocket.accept();
            // Throw the accepted client into a thread and handle the communication from within it
            new Thread(new ClientHandler(socket)).start();
        }
    }
}
