import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static BufferedWriter bufferedWriter;

    public static void main(String[] args) throws IOException {
        boolean pveMatch, loop, playerIsEven, opponentIsEven;
        int playerNumber, opponentNumber, currentRound = 0, playerScore = 0, opponentScore = 0;
        final String PLAYER, OPPONENT, HOST;
        final int PORT;

        // Get the player username and server port
        System.out.print("Enter your username: ");
        PLAYER = scanner.nextLine();
        System.out.print("Enter the desired host: ");
        HOST = scanner.nextLine().toLowerCase();
        System.out.print("Enter the server port: ");
        PORT = scanner.nextInt();

        // Try connecting to the server
        Socket socket = new Socket(HOST, PORT);

        // Creates the IO streams for communication
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        // Send the player username to the handler (server) for chat identification
        messageToClientHandler(PLAYER);
        System.out.println(bufferedReader.readLine());

        // Display the game main menu...
        System.out.println("\nWELCOME TO THE ODDS AND EVENS GAME\n1 - You X This machine (PVE)\n2 - You X Other player (PVP)");

        // ...and get the PVE or PVP match option
        System.out.print("Please type the number of the game mode you wanna play: ");
        pveMatch = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 1;
        messageToClientHandler(String.valueOf(pveMatch));

        // Get and present the opponent
        System.out.println("Waiting for an opponent...");
        OPPONENT = bufferedReader.readLine();
        System.out.println("Your opponent is " + OPPONENT + "!");

        // Open the match loop
        do {
            // Show current round
            System.out.println("\nROUND " + ++currentRound);

            do {
                // Get the choice of odd or even from the player
                System.out.print("Type 1 for odd or 2 for even: ");
                playerIsEven = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;
                messageToClientHandler(String.valueOf(playerIsEven));
                System.out.println("Waiting for " + OPPONENT + "'s input");

                // Get the choice of odd or even from the opponent
                opponentIsEven = Boolean.parseBoolean(bufferedReader.readLine());
                System.out.println(OPPONENT + (opponentIsEven ? " picked even..." : " picked odd..."));
            } while (playerIsEven == opponentIsEven);

            // Get the player number
            System.out.print("Now choose a number from 0 to 5: ");
            playerNumber = validateInput(0, 5, "Please, type a number from 0 to 5 for this field: ");
            messageToClientHandler(String.valueOf(playerNumber));
            System.out.println("Waiting for " + OPPONENT + "'s input");

            // Get the opponent number from the handler
            opponentNumber = Integer.parseInt(bufferedReader.readLine());
            System.out.println(OPPONENT + "'s number: " + opponentNumber);

            // Checks if the player won
            if (playerIsEven == ((playerNumber + opponentNumber) % 2 == 0)) {
                System.out.println("You won!");
                playerScore++;
            } else {
                System.out.println("You lost...");
                opponentScore++;
            }

            // Presents the current match score
            System.out.printf("\nCurrent score:\nYou => %d\n%s => %d\n", playerScore, OPPONENT, opponentScore);

            // Check if both players want to play again
            System.out.print("Do you want to play again? If so, type 1: ");
            messageToClientHandler(String.valueOf(scanner.nextInt() == 1));
            System.out.println("Waiting for " + OPPONENT + "'s input");
            loop = Boolean.parseBoolean(bufferedReader.readLine());
        } while (loop);

        // Closing the socket + IO streams
        bufferedReader.close();
        bufferedWriter.close();
        socket.close();
        System.out.println("GAME OVER!");
    }

    private static int validateInput(int minimalValue, int maximumValue, String incorrectInputMessage) {
        int input = scanner.nextInt();
        while (input < minimalValue || input > maximumValue) {
            System.out.print(incorrectInputMessage);
            input = scanner.nextInt();
        }
        return input;
    }

    private static void messageToClientHandler(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
