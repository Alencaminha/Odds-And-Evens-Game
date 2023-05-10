package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        // Connecting to the server
        Socket socket = new Socket("localhost", 12345);

        // Creates the client's IO streams
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Gets the client username for chat identification...
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        // ...and sends it to the client handler, completing the connection setup
        bufferedWrite(bufferedWriter, username);
        String messageReceived = bufferedReader.readLine();
        System.out.println(messageReceived);
    }

    private static void bufferedWrite(BufferedWriter bufferedWriter, String message) throws IOException{
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
