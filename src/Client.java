package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static BufferedWriter bufferedWriter;

    public static void main(String[] args) throws IOException {
        boolean pveMatch, loop, playerIsEven, opponentIsEven;
        int playerNumber, opponentNumber, currentRound = 0, playerScore = 0, opponentScore = 0;

        // Display the game main menu...
        System.out.println("""
                WELCOME TO THE ODDS AND EVENS GAME
                1 - You X This machine (PVE)
                2 - You X Other player (PVP)""");

        // ...and get the PVE or PVP match option
        System.out.print("Please type the number of the game mode you wanna play: ");
        pveMatch = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 1;

        if (pveMatch) {
            do {
                System.out.println("\nROUND " + ++currentRound);

                // Get the choice of odd or even from the player
                System.out.print("Type 1 for odd or 2 for even: ");
                playerIsEven = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;

                // Get the player number
                System.out.print("Now choose a number from 0 to 5: ");
                playerNumber = validateInput(0, 5, "Please, type a number from 0 to 5 for this field: ");

                // Generate a random number for the machine
                opponentNumber = new Random().nextInt(6);
                System.out.println("Your opponent number: " + opponentNumber);

                // Checks if the player won
                if (playerIsEven == ((playerNumber + opponentNumber) % 2 == 0)) {
                    System.out.println("You won!");
                    playerScore++;
                } else {
                    System.out.println("You lost...");
                    opponentScore++;
                }

                // Presents the current match score
                System.out.printf("""
                        
                        Current score:
                        You     => %d
                        Machine => %d
                        """, playerScore, opponentScore);

                // Check if the player want ot play again
                System.out.print("Do you want to play again? If so, type 1: ");
                loop = scanner.nextInt() == 1;
            } while (loop);
        } else {
            // Try connecting to the server
            Socket socket = new Socket("localhost", 12345);

            // Creates the IO streams for communication
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Get and send the client username for identification
            scanner.nextLine();
            System.out.print("Enter your username: ");
            messageToClientHandler(scanner.nextLine());
            System.out.println(bufferedReader.readLine());

            // Get and present the opponent
            System.out.println("Waiting for an opponent...");
            String opponentUsername = bufferedReader.readLine();
            System.out.println("Your opponent is " + opponentUsername + "!");

            // Open the match loop
            do {
                System.out.println("\nROUND " + ++currentRound);

                // Get the choice of odd or even from the player
                do {
                    System.out.print("Type 1 for odd or 2 for even: ");
                    playerIsEven = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;
                    messageToClientHandler(String.valueOf(playerIsEven));
                    opponentIsEven = Boolean.parseBoolean(bufferedReader.readLine());
                    if (opponentIsEven) System.out.println(opponentUsername + " picked even...");
                    else System.out.println(opponentUsername + " picked odd...");
                } while (playerIsEven == opponentIsEven);

                // Get the player number
                System.out.print("Now choose a number from 0 to 5: ");
                playerNumber = validateInput(0, 5, "Please, type a number from 0 to 5 for this field: ");
                messageToClientHandler(String.valueOf(playerNumber));

                // Get the opponent number from the handler
                opponentNumber = Integer.parseInt(bufferedReader.readLine());
                System.out.println("Your opponent number: " + opponentNumber);

                // Checks if the player won
                if (playerIsEven == ((playerNumber + opponentNumber) % 2 == 0)) {
                    System.out.println("You won!");
                    playerScore++;
                } else {
                    System.out.println("You lost...");
                    opponentScore++;
                }

                // Presents the current match score
                System.out.printf("""
                        
                        Current score:
                        You => %d
                        %s => %d
                        """, playerScore, opponentUsername, opponentScore);

                // Check if both players want to play again
                System.out.print("Do you want to play again? If so, type 1: ");
                messageToClientHandler(String.valueOf(scanner.nextInt() == 1));
                loop = Boolean.parseBoolean(bufferedReader.readLine());
            } while (loop);

            // Closing the socket + IO streams
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        }
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
