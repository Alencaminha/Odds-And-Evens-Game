package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final List<ClientHandler> clientsList = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private int playerNumber = 10;
    public boolean isEven, loop, loopReceived, numberReceived, isEvenReceived;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            // Get the client username and notifies the successful connection
            clientUsername = bufferedReader.readLine();
            messageToClient("Connection successful!");
            System.out.println(clientUsername + " has joined.");

            // Find and send the matched opponent
            clientsList.add(this);
            int index = clientsList.indexOf(this);
            ClientHandler opponentHandler;
            while (true) {
                System.out.print("");
                if (clientsList.size() % 2 == 0) {
                    opponentHandler = index % 2 == 0 ? clientsList.get(index + 1) : clientsList.get(index - 1);
                    break;
                }
            }
            messageToClient(opponentHandler.clientUsername);

            // Open match loop
            do {
                isEvenReceived = false;
                numberReceived = false;
                loopReceived = false;

                isEven = Boolean.parseBoolean(bufferedReader.readLine());

                // Get and send the opponent number to the player
                playerNumber = Integer.parseInt(bufferedReader.readLine());
                numberReceived = true;
                while (true) {
                    System.out.print("");
                    if (opponentHandler.numberReceived) {
                        messageToClient(String.valueOf(opponentHandler.playerNumber));
                        break;
                    }
                }

                // Check if the player will continue this match
                loop = Boolean.parseBoolean(bufferedReader.readLine());
                loopReceived = true;
                while (true) {
                    System.out.print("");
                    if (opponentHandler.loopReceived) {
                        if (!opponentHandler.loop) loop = false;
                        break;
                    }
                }
                messageToClient(String.valueOf(loop));
            } while (loop);
            closeEverything();
        } catch (IOException ioException) {
            closeEverything();
        }
    }

    private void messageToClient(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void closeEverything() {
        try {
            System.out.println(clientUsername + " has left.");
            clientsList.remove(this);
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
