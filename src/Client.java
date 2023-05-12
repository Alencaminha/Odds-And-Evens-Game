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
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Presents the player with the game main menu...
        System.out.println("""
                WELCOME TO THE ODDS AND EVENS GAME
                1 - You X This machine
                2 - You X Other player""");

        // ...and get the game mode option
        System.out.print("Please type the number of the game mode you wanna play: ");
        boolean offlineGame = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 1;

        // Setup for the game loop
        int currentRound = 0, player1Score = 0, player2Score = 0;
        boolean loop = true;
        if (offlineGame) {
            while (loop) {
                System.out.println("\nROUND " + ++currentRound);

                // Get the choice of odd or even from the player
                System.out.print("Type 1 for odd or 2 for even: ");
                boolean isEven = validateInput(1, 2, "Please, type only 1 or 2 for this field: ") == 2;

                // Get the player number
                System.out.print("Now choose a number from 0 to 5: ");
                int playerNumber = validateInput(0, 5, "Please, type a number from 0 to 5 for this field: ");

                // Generate a random number for the machine
                int machineNumber = new Random().nextInt(6);
                System.out.println("The machine's number: " + machineNumber);

                // Checks if the player won
                if (gameWinner(isEven, playerNumber, machineNumber)) {
                    System.out.println("You won!");
                    player1Score++;
                } else {
                    System.out.println("You lost...");
                    player2Score++;
                }

                // Checks if the player wants to play again
                System.out.print("Do you want to play again? If so, type 1: ");
                loop = scanner.nextInt() == 1;
            }

            // Presents the final game score
            System.out.println("Final score:" +
                    "\nPlayer 1 => " + player1Score +
                    "\nPlayer 2 => " + player2Score);
        } else {
            // Connecting to the server
            Socket socket = new Socket("localhost", 12345);

            // Creates the client's IO streams
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Gets the client username for chat identification...
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            // ...and sends it to the client handler, completing the connection setup
            bufferedWrite(bufferedWriter, username);
            String messageReceived = bufferedReader.readLine();
            System.out.println(messageReceived);

            // Sends game data to the handler

            // Closing the socket + IO streams
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        }
    }

    private static void bufferedWrite(BufferedWriter bufferedWriter, String message) throws IOException{
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private static int validateInput(int minimalValue, int maximumValue, String incorrectInputMessage) {
        Scanner scanner = new Scanner(System.in);
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
}
