/*
 * PuzzleDuzzle.java
 * Author: MFROB
 * Date: 2026-11-11
 *
 * Gets the user to solve a puzzle.
 * The player answers 4 riddles to uncover a secret coding word.
 * Type HINT at any answer prompt to get a clue. One wrong answer ends the game.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

// Defines the main class that holds all data and game logic for the riddle puzzle.
public class PuzzleDuzzle {

    // ANSI escape codes that change the console text color (RESET returns to the default color).
    static final String RED = "\u001B[31m";
    static final String BLUE = "\u001B[34m";
    static final String GREEN = "\u001B[32m";
    static final String RESET = "\u001B[0m";

    // Stores the pool of five possible four-letter secret words for the player to uncover.
    static String[] secretWords = {"LIST", "LOOP", "JAVA", "CODE", "NULL"};

    // Holds twenty coding riddles grouped in sets of four for each corresponding secret word using the formula wordIndex * 4 + letterNumber.
    static String[] riddleTexts = {
        // Contains the four riddles assigned to the first secret word, LIST.
        "Run me with a counter to visit every element. If my condition never turns false, I'm infinite. What am I?",
        "I'm the position number you put inside an array's square brackets. What am I?",
        "Binary search only works if the data is first in this state. What am I?",
        "int, double, String and boolean are all examples of me. What am I?",
        // Contains the four riddles assigned to the second secret word, LOOP.
        "Unlike an array, I can grow and shrink. ArrayList is my best-known form. What am I?",
        "Many of me can be built from one class blueprint. What am I?",
        "Several methods share one name but take different parameters. What is this called?",
        "I'm the variable in a method's header that receives the passed-in value. What am I?",
        // Contains the four riddles assigned to the third secret word, JAVA.
        "I'm the virtual machine that runs compiled Java bytecode. Three letters please.",
        "I'm a fixed-size collection where every element has the same type. What am I?",
        "In 'public static ___ main', I mean the method returns nothing. What am I?",
        "I'm a step-by-step procedure for solving a problem, like bubble sort. What am I?",
        // Contains the four riddles assigned to the fourth secret word, CODE.
        "I'm the blueprint that defines an object's fields and methods. What am I?",
        "Add 1 to the largest int and I happen, wrapping the value to a negative. What am I?",
        "I'm the process of hunting down and fixing errors in a program. What am I?",
        "I'm what Java throws when something goes wrong at runtime. try/catch handles me. What am I?",
        // Contains the four riddles assigned to the fifth secret word, NULL.
        "I'm the keyword that allocates memory for a new object. What am I?",
        "I'm the character standard that lets Java's char hold nearly any language. What am I?",
        "I'm the search that checks each element one by one and needs no sorted data. What am I?",
        "I'm the whole-number type that holds values far bigger than int. What am I?"
    };

    // Stores the exact lowercase answers aligned by index with the riddles in riddleTexts.
    static String[] answers = {
        "loop", "index", "sorted", "type",
        "list", "object", "overloading", "parameter",
        "jvm", "array", "void", "algorithm",
        "class", "overflow", "debugging", "exception",
        "new", "unicode", "linear", "long"
    };

    // Contains optional clues matched by index to help the player solve each riddle.
    static String[] hints = {
        "Think 'for' and 'while'.", "In arr[3], the 3 is me.",
        "Arrays.sort puts data in this state.", "Four letters. Declared before a variable name.",
        "ArrayList is one of me.", "new Scanner(System.in) creates one of me.",
        "Think of println. Ends in -ing.", "Starts with 'param'.",
        "Think 'Virtual Machine'.", "int[] nums = new int[10];",
        "public static ___ main(String[] args)", "Starts with 'algo'.",
        "public ___ MyProgram {", "Integer.MAX_VALUE + 1 goes negative.",
        "What you do with a bug. Ends in -ing.", "Starts with 'ex'.",
        "Scanner in = ___ Scanner(System.in);", "Starts with 'uni'.",
        "Compared to binary search, I check one by one.", "Four letters. Bigger than int."
    };

    // Tracks the letters of the secret word that have been unlocked so far during the round.
    static ArrayList<Character> revealed = new ArrayList<>();
    // Counts how many hints the player requests so the total can be saved at the end.
    static int hintsUsed = 0;
    // Becomes true if the player correctly guesses the secret word before finishing all four riddles.
    static boolean solvedEarly = false;

    // Displays the hint for riddle index r and increases the hint counter by one.
    static void cluesGiven(int r) {
        // Prints the matching hint string from the hints array to the console.
        System.out.println("Hint: " + hints[r]);
        // Increments the total number of hints used during this game session.
        hintsUsed++;
    }

    // Compares the player's formatted guess against the stored answer at index r and returns true if they match.
    static boolean answerYouWish(int r, String guess) {
        // Checks string equality between the user's guess and the target answer.
        return guess.equals(answers[r]);
    }

    // Runs a single riddle and returns true if the player answered correctly, false if they got it wrong.
    static boolean riddles(int r, Scanner in, String word) {
        // Prints the riddle text at index r on a new line.
        System.out.println("\n" + BLUE + riddleTexts[r] + RESET);
        // Remembers whether a hint was already counted for this riddle so repeats are not double counted.
        boolean hintShown = false;

        // Keeps asking until the player gives a real answer, since typing HINT does not use up their answer.
        while (true) {
            // Prompts the player for their answer and tells them how to get a hint.
            System.out.println(GREEN + "HINT for a clue, GUESS to name the secret word" + RESET);
            System.out.println(GREEN + "Your answer is: " + RESET);
            // Reads the input, removes extra spaces, and converts it to lowercase for fair comparison.
            String guess = in.nextLine().trim().toLowerCase();

            // Lets the player try the secret word early instead of answering this riddle.
            if (guess.equals("guess") || guess.startsWith("guess ")) {
                // Takes anything typed after GUESS on the same line (e.g. "GUESS JAVA") as the word.
                String early = guess.substring(5).trim().toUpperCase(); //
                // Asks for the word separately if nothing was typed after GUESS.
                if (early.isEmpty()) {
                    System.out.print("What is the secret word? ");
                    early = in.nextLine().trim().toUpperCase();
                }
                // Marks the game as solved early if the word is right.
                if (early.equals(word)) {
                    solvedEarly = true;
                    return true;
                }
                // A wrong early guess fails the game, same as a wrong riddle answer.
                System.out.println("Wrong! The word was " + word + ".");
                return false;
            }

            // Checks if the player asked for a hint instead of answering.
            if (guess.equals("hint")) {
                // Shows and counts the hint the first time it is requested for this riddle.
                if (!hintShown) {
                    cluesGiven(r);
                    hintShown = true;
                } else {
                    // Repeats the same hint without counting it again.
                    System.out.println("Hint: " + hints[r]);
                }
                // Goes back to the top of the loop to ask for the answer again.
                continue;
            }

            // Checks if the cleaned guess matches the correct answer for this riddle.
            if (answerYouWish(r, guess)) {
                // Congratulates the player when their guess is right.
                System.out.println("Correct!");
                return true;
            }

            // Reveals the correct answer and fails the round since only one answer is allowed.
            System.out.println("Wrong! The answer was: " + answers[r]);
            return false;
        }
    }

    // Displays the player's current progress on the secret word using unlocked letters and underscores.
    static void showProgress(String word) {
        // Prints the label before displaying the individual letter slots.
        System.out.print("Secret word: ");
        // Iterates through every character position in the secret word.
        for (int i = 0; i < word.length(); i++) {
            // Checks whether the letter at position i has already been unlocked in the revealed list.
            if (i < revealed.size()) {
                // Prints the unlocked character followed by a space if it has been revealed.
                System.out.print(revealed.get(i) + " ");
            } else {
                // Prints an underscore placeholder for any letter that is still hidden.
                System.out.print("_ ");
            }
        }
        // Moves the console cursor to a new line after printing all letter slots.
        System.out.println();
    }

    // Appends the secret word, outcome, and total hints used to an external text file.
    static void saveResult(String word, String outcome) {
        // Opens riddle_results.txt in append mode using try-with-resources so the file closes automatically.
        try (PrintWriter out = new PrintWriter(new FileWriter("riddle_results.txt", true))) {
            // Writes a formatted summary line containing the word, result, and hint count to the file.
            out.println("Word: " + word + " | Result: " + outcome + " | Hints used: " + hintsUsed);
        } catch (IOException e) {
            // Catches any file writing errors and prints a safe message instead of crashing the program.
            System.out.println("Could not write to file.");
        }
    }

    // Serves as the entry point that initializes the game, runs four riddles, checks the final word, and saves results.
    public static void main(String[] args) {
        // Creates a Scanner object to read user input from the keyboard.
        Scanner in = new Scanner(System.in);
        // Generates a random index between zero and the length of secretWords minus one.
        int wordIndex = new Random().nextInt(secretWords.length);
        // Selects the secret word corresponding to the randomly generated index.
        String word = secretWords[wordIndex];

        // Prints the welcome instructions explaining the goal and the rules of the game.
        System.out.println(RED + "Answer 4 riddles to uncover a coding word!");
        System.out.println("Type HINT for a clue, or GUESS to name the secret word early. One wrong answer and you lose." + RESET);

        // Loops four times to ask four riddles and unlock one letter of the secret word per round.
        for (int n = 0; n < 4; n++) {
            // Runs the riddle and checks whether the player got it right.
            if (!riddles(wordIndex * 4 + n, in, word)) {
                // Ends the game immediately with a loss message if the answer was wrong.
                System.out.println("\nGame over! You failed to crack the code word.");
                // Saves the failed result before exiting.
                saveResult(word, "FAILED");
                in.close();
                return;
            }
            // Stops asking riddles if the player already named the secret word.
            if (solvedEarly) {
                break;
            }
            // Adds the nth character of the secret word to the revealed list after a correct answer.
            revealed.add(word.charAt(n));
            // Updates the screen to show the newly revealed letter alongside any remaining blanks.
            showProgress(word);
        }

        // Tracks whether the player has won, starting true if they solved the word early.
        boolean won = solvedEarly;

        // Only asks for the final guess if the word was not already solved early.
        if (!solvedEarly) {
            // Prompts the player to guess the complete four-letter secret word.
            System.out.print("\nWhat is the secret word? ");
            // Reads the final word guess, trims whitespace, and converts it to uppercase to match secretWords.
            won = in.nextLine().trim().toUpperCase().equals(word);
        }

        // Prints the outcome and saves it to the results file.
        if (won) {
            System.out.println("You cracked the code word: " + word + "!");
            saveResult(word, solvedEarly ? "WON (early)" : "WON");
        } else {
            System.out.println("Game over! The word was " + word + ".");
            saveResult(word, "FAILED");
        }

        // Closes the Scanner to release system input resources before the program exits.
        in.close();
    }
}