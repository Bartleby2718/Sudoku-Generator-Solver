import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

//TODO: Consider edge cases?
public class SudokuSolver {
    public static int[][] solve(int[][] grid) {
        // Declaration/Initialization of local variables
        int num, index, squareRoot, row, col, counter, min, rowSquare, colSquare;
        index = grid.length; // for debugging purposes
        int length = grid.length;
        squareRoot = Math.round((long) Math.sqrt(length));
        MarkupGrid markupGrid = new MarkupGrid(length);

        // For every entry given, update markups accordingly
        for (int i = 0; i < length; ++i)
            for (int j = 0; j < length; ++j) {
                // Skip if the entry is not known
                if (grid[i][j] == 0) continue;
                // Now that the entry is known(nonzero)...
                // Get the number
                num = grid[i][j];
                // Get the corresponding index
                index = num - 1;
                // INVALID IF THIS NUMBER IS NOT IN THE MARKUP
                if (markupGrid.getMarkupGrid()[i][j][index] == 0)
                    return new int[length][length];
                // Otherwise, update markupGrid
                markupGrid.update(i, j, num);
            }

        // Declare and initialize stacks
        Stack<int[][]> possibilities = new Stack<int[][]>();
        Stack<MarkupGrid> listOfMarkupGrids = new Stack<MarkupGrid>();
        possibilities.push(grid);
        listOfMarkupGrids.push(markupGrid);

        // Repeat until the stack is empty
        while (!possibilities.isEmpty()) {
            grid = possibilities.pop();
            markupGrid = listOfMarkupGrids.pop();
            boolean full = false; // full being true means "There is no blank cell"
            boolean valid = true; // valid being true means "There is no blank cell with 0 candidate."
            boolean done = false; // done being true means "There is no blank cell with 1 candidate."
            while (valid && !done && !full) { // Repeat until the grid is full or there is no blank cell with 0/1 candidate
                full = true; // Full unless we find a blank cell
                valid = true; // Valid unless we find a blank cell with 0 candidate
                done = true; // Done unless we find a blank cell with 1 candidate
                // Iterate over all cells
                for (int i = 0; i < length; ++i) {
                    for (int j = 0; j < length; ++j) {
                        // Pass if the entry is known
                        if (grid[i][j] != 0) continue;
                        full = false; // Some entry is not known yet!
                        // Count the number of candidates for that cell
                        counter = 0;
                        for (int k = 0; k < length; ++k)
                            if (markupGrid.getMarkupGrid()[i][j][k] == 1) {
                                counter++;
                                index = k;
                            }
                        // 0 candidate
                        if (counter == 0) { // Something must be wrong
                            valid = false;
                            break;
                            // 1 candidate
                        } else if (counter == 1) {
                            // Put in the only candidate
                            num = index + 1; // because num is the entry
                            grid[i][j] = num;
                            markupGrid.update(i, j, num);
                            done = false;
                            break;
                        }
                    }
                }
            }
            if (full) return grid; // Done if full // TODO MAY NEED TO CHECK VALIDITY
            if (!valid) continue; // Move on to the next one if invalid
            // Done filling out trivial cells and valid so far!
            // Find the cell with the fewest candidates (linear search)
            min = length;
            row = -1; // for debugging purposes
            col = -1; // for debugging purposes
            outerLoop:
            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < length; ++j) {
                    counter = 0;
                    // Skip blank cells
                    if (grid[i][j] != 0) continue;
                    for (int k = 0; k < length; ++k)
                        if (markupGrid.getMarkupGrid()[i][j][k] == 1) counter++;
                    if (counter > 0 && counter < min) {
                        min = counter;
                        row = i;
                        col = j;
                        // Exit when a cell with two candidates is found
                        if (counter == 2) break outerLoop;
                    }
                }
            }
            // Try all candidates
            ArrayList<Integer> listOfCandidates = SudokuSolver.markupToArrayList(markupGrid.getMarkupGrid()[row][col]);
            for (int k : listOfCandidates) {
                // Update grid and push it to the stack
                int[][] newGrid = new int[length][length];
                for (int i = 0; i < length; ++i)
                    System.arraycopy(grid[i], 0, newGrid[i], 0, length);
                newGrid[row][col] = k;
                possibilities.push(newGrid);
                // Update markupGrid and push it to the stack
                MarkupGrid newMarkupGrid = new MarkupGrid(markupGrid);
                newMarkupGrid.update(row, col, k);
                listOfMarkupGrids.push(newMarkupGrid);
            }
        }
        // The stack is empty if you get here
        return new int[length][length];
    }

    public static ArrayList<Integer> markupToArrayList(int[] markup) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < markup.length; i++)
            if (markup[i] == 1)
                list.add(i + 1); // i is an index; we want the entries
        return list;
    }

    public static int[][] processInput(String fileName, int square, boolean samurai) throws FileNotFoundException {
        int squareRoot = Math.round((long) Math.sqrt(square));
        Scanner fileScanner = new Scanner(new File(fileName));
        int[][] grid;
        if (!samurai) {
            grid = new int[square][square];
            int i = 0;
            while (fileScanner.hasNextLine()) {
                // Trim leading/trailing whitespace
                String line = fileScanner.nextLine().trim();
                if (line.length() == 0)
                    continue;
                String[] data = line.split("   ");
                for (int j = 0; j < squareRoot; j++) {
                    String[] data2 = data[j].split(" ");
                    for (int k = 0; k < squareRoot; k++)
                        grid[i][squareRoot * j + k] = Integer.parseInt(data2[k]);
                }
                i++;
            }
        } else {
            grid = new int[2 * square + squareRoot][2 * square + squareRoot];
            int i = 0;
            while (fileScanner.hasNextLine()) {
                // Trim leading/trailing whitespace
                String line = fileScanner.nextLine().trim();
                if (line.length() == 0)
                    continue;
                String[] data = line.split("   ");
                for (int j = 0; j < data.length; j++) {
                    String[] data2 = data[j].split(" ");
                    for (int k = 0; k < squareRoot; k++) {
                        if (data2[k].equals("b")) // Blank indicator
                            grid[i][squareRoot * j + k] = -1;
                        else
                            grid[i][squareRoot * j + k] = Integer.parseInt(data2[k]);
                    }
                }
                i++;
            }
        }
        fileScanner.close();
        return grid;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner keyboard = new Scanner(System.in);
        int square, squareRoot;
        do {
            System.out.print("Enter a perfect square: ");
            square = keyboard.nextInt();
            squareRoot = Math.round((long) Math.sqrt(square));
            if (square == squareRoot * squareRoot)
                break;
        } while (true);
        boolean samurai;
        do {
            System.out.print("Is this Samurai Sudoku? (y/n): ");
            String answer = keyboard.next();
            if (answer.equalsIgnoreCase("y")) {
                samurai = true;
                break;
            } else if (answer.equalsIgnoreCase("n")) {
                samurai = false;
                break;
            }
        } while (true);
        String fileName;
        do {
            System.out.print("Enter a file name: ");
            fileName = keyboard.next();
        } while (!new File(fileName).exists());
        System.out.println("Now look at the pop-up!");
        keyboard.close();
        int[][] grid = processInput(fileName, square, samurai);
        JFrame frame;
        if (samurai)
            frame = new JFrame("Samurai Sudoku Solver");
        else
            frame = new JFrame("Regular Sudoku Solver");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new Panel(grid, samurai, square));
        frame.setVisible(true);
    }
}