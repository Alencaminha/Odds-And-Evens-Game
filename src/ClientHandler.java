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
    public boolean isEven;
    public int playerNumber = 10;

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
            clientsList.add(this);
            bufferedWrite("Connection successful!");
            System.out.println(clientUsername + " has joined.");

            // Get and send the matched opponent
            ClientHandler opponent = getOpponent();
            String opponentUsername = opponent.clientUsername;
            bufferedWrite(opponentUsername);

            while (!socket.isClosed()) {
                // Get the player inputs
                isEven = Boolean.parseBoolean(bufferedReader.readLine());
                playerNumber = Integer.parseInt(bufferedReader.readLine());

                // Get and send the opponent number
                int opponentNumber = opponent.playerNumber;
                bufferedWrite(String.valueOf(opponentNumber));

                if (!Boolean.parseBoolean(bufferedReader.readLine())) {
                    closeEverything();
                    break;
                }
            }
        } catch (IOException ioException) {
            closeEverything();
        }
    }

    private ClientHandler getOpponent() {
        int thisIndex = clientsList.indexOf(this);
        // Check if there is an even number of connected players
        while (true) if (clientsList.size() % 2 == 0) {
            return thisIndex % 2 == 0 ? clientsList.get(thisIndex + 1) : clientsList.get(thisIndex - 1);
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
