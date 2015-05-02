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
    private ArrayList<JTextField> textBoxes;

    public Panel(int[][] grid) {
        setLayout(new BorderLayout());
        this.grid = grid;
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
                String text = "";
                if (puzzle[i][j] != 0)
                    text += puzzle[i][j];
                JTextField tf = new JTextField(text);
                textBoxes.add(tf);
                gridSpace.add(tf);
            }
        revalidate();
        repaint();
    }

    public int getValueAt(int i, int j) {
        if (i * length + j >= textBoxes.size())
            return 0;
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

    private class solveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateGrid();
            // Print and save the start time
            Date startTime = new Date();
            System.out.println("Started at " + startTime);
            // Run SudokuSolver
            int[][] solution = SudokuSolver.solve(grid);
            // Print and save end time
            Date endTime = new Date();
            System.out.println("Ended at " + endTime);
            // Get the time difference (unit: seconds)
            double timeElapsed = (endTime.getTime() - startTime.getTime()) / 1000.0;
            if (solution[0][0] == 0)// Infeasible sudoku
                JOptionPane.showMessageDialog(null, "This sudoku is not feasible.\n(Took " + timeElapsed + " seconds.)");
            else // Feasible sudoku
                JOptionPane.showMessageDialog(null, "Done! Click OK to see result!\n(Took " + timeElapsed + " seconds.)");
            // Display solution
            grid = solution;
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