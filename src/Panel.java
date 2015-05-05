import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("serial")
public class Panel extends JPanel {
    private int[][] grid;
    private final boolean isSamurai;
    private final int square; // Number of cells in each square
    private final int root;
    private final int length; // Total length of grid
    private ArrayList<JTextField> textBoxes;
    private JPanel gridSpace, buttonSpace;
    private JButton solveButton, generateButton;

    public Panel(int[][] grid, boolean isSamurai, int square) {
        setLayout(new BorderLayout());
        this.grid = grid;
        this.isSamurai = isSamurai;
        this.square = square;
        this.root = Math.round((long) Math.sqrt(square));
        this.length = grid.length;

        textBoxes = new ArrayList<JTextField>();
        solveButton = new JButton("Solve Puzzle");
        solveButton.addActionListener(new solveListener());
        generateButton = new JButton("Generate Sudoku");
        generateButton.addActionListener(new GenerateListener());
        
        gridSpace = new JPanel();
        gridSpace.setLayout(new GridLayout(length, length));
        buttonSpace = new JPanel();
        buttonSpace.setLayout(new GridLayout(1, 2));
        buttonSpace.add(solveButton);
        buttonSpace.add(generateButton);
        showPuzzle();
        add(gridSpace);
        add(buttonSpace, BorderLayout.SOUTH);
    }

    private void showPuzzle() {
    	gridSpace.removeAll();
        /*gridSpace = new JPanel();
        gridSpace.setLayout(new GridLayout(length, length));*/
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++) {
                JTextField tf = new JTextField();
                tf.setEditable(false);
                String text = "";
                if (grid[i][j] == -1)
                    tf.setBackground(Color.BLACK);
                else {
                    if (grid[i][j] > 0)
                        text += grid[i][j];
                    if (((i / root + j / root) % 2 == 0))
                        tf.setBackground(Color.LIGHT_GRAY);
                }
                tf.setText(text);
                tf.setHorizontalAlignment(JTextField.CENTER);
                textBoxes.add(tf);
                gridSpace.add(tf);
            }
        revalidate();
        repaint();
    }

    /*private int getValueAt(int i, int j) {
        if (i * length + j >= textBoxes.size())
            return 0;
        if (grid[i][j] == -1)
            return -1;
        String str = textBoxes.get(i * length + j).getText();
        if (str.equals(""))
            return 0;
        return Integer.parseInt(str);
    }*/

    /*private void updateGrid() {
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                grid[i][j] = getValueAt(i, j);
    }*/
    
    //private void update

    private class solveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //updateGrid();
            // Print and save the start time
            Date startTime = new Date();
            System.out.println("Started at " + startTime);
            // Run SudokuSolver
            grid = SudokuSolver.solve(grid, isSamurai, square);
            // Print and save end time
            Date endTime = new Date();
            System.out.println("Ended at " + endTime);
            // Get the time difference (unit: seconds)
            double timeElapsed = (endTime.getTime() - startTime.getTime()) / 1000.0;
            // Check validity
            boolean valid = true;
            if (grid == null)
                valid = false;
            else
                for (int i = 0; i < length; i++)
                    for (int j = 0; j < length; j++)
                        if (grid[i][j] == 0)// Infeasible sudoku
                            valid = false;
            String message;
            if (valid) {
                showPuzzle();
                message = "Done!\n(Took " + timeElapsed + " seconds.)";
            } else
                message = "This sudoku is not feasible.\n(Took " + timeElapsed + " seconds.)";
            JOptionPane.showMessageDialog(null, message);
            System.out.println("Took " + timeElapsed + " seconds.");
        }
    }
    
    private class GenerateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			double clueProportion;
			do {
			clueProportion = Double.parseDouble(JOptionPane.showInputDialog(null, "What proportion of clues do you want revealed?"));
			} while (clueProportion < 0 || clueProportion > 1);
			if (!isSamurai)
				grid = SudokuGenerator.generateSudoku(length, clueProportion);
			else
				grid = SudokuGenerator.generateSamurai(square, clueProportion);
			showPuzzle();
		}
    	
    }
}