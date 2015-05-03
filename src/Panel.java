import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("serial")
public class Panel extends JPanel {
    private JButton solve;
    private JPanel gridSpace, buttonSpace;
    private int[][] grid;
    private int length; // Total length of grid
    private int square;
    private ArrayList<JTextField> textBoxes;
    private boolean samurai;

    public Panel(int[][] grid, boolean samurai, int square) {
        setLayout(new BorderLayout());
        this.grid = grid;
        this.samurai = samurai;
        this.square = square;
        textBoxes = new ArrayList<JTextField>();
        length = grid.length;
        solve = new JButton("Solve Puzzle");
        solve.addActionListener(new solveListener());
        /*resize = new JButton("Change Length");
        resize.addActionListener(new resizeListener());
        generateRegular = new JButton("Generate Sudoku Puzzle");
        generateRegular.addActionListener(new generateRegularListener());
        generateSamurai = new JButton("Generate Samurai Sudoku Puzzle");
        generateSamurai.addActionListener(new generateSamuraiListener());*/
        gridSpace = new JPanel();
        gridSpace.setLayout(new GridLayout(grid.length, grid.length));
        buttonSpace = new JPanel();
        buttonSpace.setLayout(new GridLayout(1, 1));
        buttonSpace.add(solve);
        //buttonSpace.add(resize);
        showPuzzle(grid);
        add(gridSpace);
        add(buttonSpace, BorderLayout.SOUTH);
        //add(generateRegular, BorderLayout.SOUTH);
        //add(generateSamurai, BorderLayout.SOUTH);
    }

    public void showPuzzle(int[][] puzzle) {
        gridSpace.removeAll();
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++) {
            	JTextField tf = new JTextField();
                String text = "";
                if (puzzle[i][j] == -1)
                	tf.setBackground(Color.BLACK);
                else if (puzzle[i][j] != 0)
                    text += puzzle[i][j];
                tf.setText(text);
                textBoxes.add(tf);
                gridSpace.add(tf);
            }
        revalidate();
        repaint();
    }

    public int getValueAt(int i, int j) {
        if (i * length + j >= textBoxes.size())
            return 0;
        if (grid[i][j] == -1)
        	return -1;
        String str = textBoxes.get(i * length + j).getText();
        if (str.equals(""))
            return 0;
        return Integer.parseInt(str);
    }

    public void updateGrid() {
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                grid[i][j] = getValueAt(i, j);
    }
    
    // Returns Sudoku puzzle within the Samurai Sudoku with top left corner (i,j)
    public int[][] subSudoku(int i, int j) {
    	int[][] subSudoku = new int[square][square];
    	for (int x = 0; x < square; x++)
    		for (int y = 0; y < square; y++)
    			subSudoku[x][y] = grid[x + i][y + j];
    	return subSudoku;
    }

    private class solveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateGrid();
            // Print and save the start time
            Date startTime = new Date();
            System.out.println("Started at " + startTime);
            // Run SudokuSolver
            //int[][] solution = null;
            if (!samurai)
            	grid = SudokuSolver.solve(grid);
            else {
            	int root = (int)Math.sqrt(square);
            	int[][] solution1 = SudokuSolver.solve(subSudoku(0, 0));
            	for (int i = 0; i < square; i++)
            		for (int j = 0; j < square; j++)
            			grid[i][j] = solution1[i][j];
            	int[][] solution2 = SudokuSolver.solve(subSudoku(0, square + root));
            	for (int i = 0; i < square; i++)
            		for (int j = 0; j < square; j++)
            			grid[i][j + square + root] = solution2[i][j];
            	int[][] solution4 = SudokuSolver.solve(subSudoku(square + root, 0));
            	for (int i = 0; i < square; i++)
            		for (int j = 0; j < square; j++)
            			grid[i + square + root][j] = solution4[i][j];
            	int[][] solution5 = SudokuSolver.solve(subSudoku(square + root, square + root));
            	for (int i = 0; i < square; i++)
            		for (int j = 0; j < square; j++)
            			grid[i + square + root][j + square + root] = solution5[i][j];
            	int[][] solution3 = SudokuSolver.solve(subSudoku(square - root, square - root));
            	for (int i = 0; i < square; i++)
            		for (int j = 0; j < square; j++)
            			grid[i + square - root][j + square - root] = solution3[i][j];
            }
            // Print and save end time
            Date endTime = new Date();
            System.out.println("Ended at " + endTime);
            // Get the time difference (unit: seconds)
            double timeElapsed = (endTime.getTime() - startTime.getTime()) / 1000.0;
            boolean valid = true;
            for (int i = 0; i < grid.length; i++)
            	for (int j = 0; j < grid.length; j++)
            		if (grid[i][j] == 0)// Infeasible sudoku
            			valid = false;
            if (!valid)
            	JOptionPane.showMessageDialog(null, "This sudoku is not feasible.\n(Took " + timeElapsed + " seconds.)");
            else 
            	JOptionPane.showMessageDialog(null, "Done! Click OK to see result!\n(Took " + timeElapsed + " seconds.)");
            // Display solution
            /*for (int i = 0; i < grid.length; i++) {
            	for (int j = 0; j < grid.length; j++)
            		System.out.print(grid[i][j] + " ");
            	System.out.println();
            }*/
            showPuzzle(grid);
        }
    }

    /*private class resizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	length = Integer.parseInt(JOptionPane.showInputDialog("Enter new edge length for the grid"));
        	gridSpace.setLayout(new GridLayout(length, length));
        	updateGrid();
        	showPuzzle(grid);
        }
    }*/

    /*private class generateSamuraiListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }*/
}