package src;

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
        // Try connecting to the server
        Socket socket = new Socket("localhost", 12345);

        // Creates the IO streams for communication
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Get and send the client username for identification
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        messageToClientHandler(username);
        System.out.println(bufferedReader.readLine());

        // Display the game main menu...
        System.out.println("""
                
                WELCOME TO THE ODDS AND EVENS GAME
                1 - You X This machine (PVE)
                2 - You X Other player (PVP)""");

        // ...and get the PVE or PVP match option
        System.out.print("Please type the number of the game mode you wanna play: ");
        boolean pvpMatch = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;
        messageToClientHandler(String.valueOf(pvpMatch));

        // Get and present the opponent in the case of a PVP match
        String opponentUsername = "Machine";
        if (pvpMatch) {
            System.out.println("Waiting for an opponent...");
            opponentUsername = bufferedReader.readLine();
            System.out.println("Your opponent is " + opponentUsername + "!");
        }

        // Open the match loop
        int currentRound = 0, player1Score = 0, player2Score = 0;
        boolean loop = true;
        while (loop) {
            System.out.println("\nROUND " + ++currentRound);

            // Get the choice of odd or even from the player
            System.out.print("Type 1 for odd or 2 for even: ");
            boolean isEven = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;

            // Get the player number
            System.out.print("Now choose a number from 0 to 5: ");
            int playerNumber = validateInput(0, 5, "Please, type a number from 0 to 5 for this field: ");

            // Get the opponent number
            int opponentNumber;
            if (pvpMatch) {
                // Trade data with the client handler
                messageToClientHandler(String.valueOf(isEven));
                messageToClientHandler(String.valueOf(playerNumber));
            }
            opponentNumber = Integer.parseInt(bufferedReader.readLine());
            System.out.println("Your opponent number: " + opponentNumber);

            // Checks if the player won
            if (gameWinner(isEven, playerNumber, opponentNumber)) {
                System.out.println("You won!");
                player1Score++;
            } else {
                System.out.println("You lost...");
                player2Score++;
            }

            // Presents the final game score
            System.out.printf("""
                        
                        Current score:
                        You => %d
                        %s => %d
                        """, player1Score, opponentUsername, player2Score);

            // Checks if the player wants to play again
            System.out.print("Do you want to play again? If so, type 1: ");
            loop = scanner.nextInt() == 1;
            messageToClientHandler(String.valueOf(loop));
        }

        // Closing the socket + IO streams
        bufferedReader.close();
        bufferedWriter.close();
        socket.close();
    }

    private static int validateInput(int minimalValue, int maximumValue, String incorrectInputMessage) {
        int input = scanner.nextInt();
        while (input < minimalValue || input > maximumValue) {
            System.out.print(incorrectInputMessage);
            input = scanner.nextInt();
        }
        return input;
    }

    private static boolean gameWinner(boolean player1Even, int player1Number, int player2Number) {
        return player1Even == ((player1Number + player2Number) % 2 == 0);
    }

    private static void messageToClientHandler(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
