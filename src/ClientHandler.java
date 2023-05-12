package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            // Get the client username as soon as they connect with the server...
            this.clientUsername = bufferedReader.readLine();

            // ...and notifies the client and server that the connection was successful
            bufferedWrite("Server: Connection successful.");
            System.out.println(clientUsername + " has joined.");
        } catch (IOException ioException) {
            closeEverything();
        }
    }

    private void bufferedWrite(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void closeEverything() {
        try {
            System.out.println(clientUsername + " has left.");
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
