import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String message = "Enter 0 if you want to enter the name of an input file.";
        message += "\nEnter 1 if you want to generate a random sudoku.";
        int choice = Utils.answer0or1(message);
        // 0: input file, 1: generate one.
        if (choice == 0) processInputFile();
        else processUserInput();
    }

    public static void processInputFile() throws FileNotFoundException {
        int[][] grid;
        boolean isSamurai;
        int length, square, root, val;
        String fileName;
        String message = "Enter the name of an input file.";
        message += "\n(Don't forget to put .txt at the end.)";
        do {
            fileName = JOptionPane.showInputDialog(null, message);
        } while (!new File(fileName).exists());
        length = 0;
        // First run: get length
        Scanner fileScanner = new Scanner(new File(fileName));
        while (fileScanner.hasNextLine())
            if (fileScanner.nextLine().trim().length() != 0)
                length++;
        fileScanner.close();
        // Second run: get square and isSamurai
        isSamurai = false;// This line is required just for initialization
        square = length;// This line is required just for initialization
        fileScanner = new Scanner(new File(fileName));
        grid = new int[length][length];
        int i = 0;
        while (fileScanner.hasNextLine()) {
            // Trim leading/trailing whitespace
            String line = fileScanner.nextLine().trim();
            // Skip blank lines
            if (line.length() == 0) continue;
            // Each square is delimited by three spaces
            String[] data = line.split("   ");
            for (int j = 0; j < data.length; j++) {
                // Each entry in a square is delimited by a single space
                String[] data2 = data[j].split(" ");
                // This is the square root of the number of cells in a square
                root = data2.length;
                for (int k = 0; k < root; k++) {
                    // -1 indicates a black cell
                    if (data2[k].equals("-1")) {
                        // It must be a Samurai Sudoku
                        isSamurai = true;
                        // Fill in -1 if black
                        grid[i][root * j + k] = -1;
                    } else {
                        val = Integer.parseInt(data2[k]);
                        square = root * root;
                        // Guarantees that all numbers in the input file are between 1 and square inclusive (except for "-1")
                        if (val >= 0 && val <= square)
                            grid[i][root * j + k] = val;
                        else
                            throw new IllegalArgumentException("Some entry is invalid in the input file.");
                    }
                }
            }
            i++;
        }
        fileScanner.close();
        JFrame frame = new JFrame(Utils.determineType(isSamurai) + " Solver");
        frame.setSize(650, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new Panel(grid, isSamurai, square));
        frame.setVisible(true);
    }

    public static void processUserInput() throws IOException {
        int[][] grid;
        boolean isSamurai;
        int length, square, type;
        double clueProportion;
        type = Utils.answer0or1("Enter 0 if you want a regular sudoku.\nEnter 1 if you want a samurai sudoku.");
        isSamurai = (type == 1);
        if (!isSamurai) { // regular sudoku
            square = Utils.askSquare("What is the dimension of the regular sudoku?\n(It must be a perfect square.)");
            clueProportion = Utils.askProportion();
            grid = SudokuGenerator.generateRegular(square, clueProportion);
        } else {// Samurai Sudoku
            square = Utils.askSquare("A samurai sudoku consists of several squares.\nHow many cells are there in each square?");
            length = Utils.askDimSamurai(square);
            clueProportion = Utils.askProportion();
            grid = SudokuGenerator.generateSamurai(square, length, clueProportion);
        }
        JFrame frame = new JFrame(Utils.determineType(isSamurai) + " Sudoku Solver");
        frame.setSize(650, 650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new Panel(grid, isSamurai, square));
        frame.setVisible(true);
    }
}