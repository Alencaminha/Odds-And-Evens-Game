package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientHandler implements Runnable {
    private static final List<ClientHandler> clientsList = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private int playerNumber = 10;
    public boolean isEven;

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

            // Open game loop
            while (!socket.isClosed()) {
                // Check if the match will be a PVP
                boolean pvpMatch = Boolean.parseBoolean(bufferedReader.readLine());
                ClientHandler opponent = null;
                if (pvpMatch) {
                    clientsList.add(this);
                    // Find and send the matched opponent
                    int index = clientsList.indexOf(this);
                    while (true) if (clientsList.size() % 2 == 0) {
                        opponent = index % 2 == 0 ? clientsList.get(index + 1) : clientsList.get(index - 1);
                        break;
                    }
                    messageToClient(opponent.clientUsername);
                }

                // Open match loop
                boolean loop = true;
                while (loop) {
                    // Get and send the opponent number to the player
                    int opponentNumber;
                    if (pvpMatch) {
                        isEven = Boolean.parseBoolean(bufferedReader.readLine());
                        playerNumber = Integer.parseInt(bufferedReader.readLine());
                        opponentNumber = opponent.playerNumber;
                    } else {
                        // Generate a random number for the machine
                        opponentNumber = new Random().nextInt(6);
                    }
                    messageToClient(String.valueOf(opponentNumber));

                    // Check if the player will continue this match
                    loop = Boolean.parseBoolean(bufferedReader.readLine());
                }
                closeEverything();
            }
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
