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
    private boolean isEven, loop, loopReceived, numberReceived, isEvenReceived;
    private ClientHandler opponentHandler;

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

            // Check if the player will play against the server or another player
            boolean pvpMatch = !Boolean.parseBoolean(bufferedReader.readLine());

            // Get who the opponent will be
            String opponentUsername;
            if (pvpMatch) {
                // Find and send the matched opponent for the PvP
                clientsList.add(this);
                int index = clientsList.indexOf(this);
                while (true) {
                    System.out.print("");
                    if (clientsList.size() % 2 == 0) {
                        opponentHandler = index % 2 == 0 ? clientsList.get(index + 1) : clientsList.get(index - 1);
                        break;
                    }
                }
                opponentUsername = opponentHandler.clientUsername;
            } else {
                // 'the server' will be the server name for the PvE
                opponentUsername = "the server";
            }
            messageToClient(opponentUsername);

            // Open match loop
            do {
                if (!pvpMatch) {
                    // If the player chose even, the machine chooses odd, and vice versa
                    isEven = Boolean.parseBoolean(bufferedReader.readLine());
                    messageToClient(String.valueOf(!isEven));

                    // Send a random number to the player
                    playerNumber = Integer.parseInt(bufferedReader.readLine());
                    messageToClient(String.valueOf(new Random().nextInt(6)));

                    // Send to the player that the machine will play until they want to stop
                    loop = Boolean.parseBoolean(bufferedReader.readLine());
                    messageToClient(String.valueOf(loop));
                } else {
                    isEvenReceived = false;
                    numberReceived = false;
                    loopReceived = false;

                    // Determines who picks the odd or even choice first
                    do {
                        isEven = Boolean.parseBoolean(bufferedReader.readLine());
                        isEvenReceived = true;
                        while (true) {
                            System.out.print("");
                            if (opponentHandler.isEvenReceived) {
                                messageToClient(String.valueOf(opponentHandler.isEven));
                                break;
                            }
                        }
                        isEvenReceived = false;
                    } while (isEven == opponentHandler.isEven);

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
                }
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
