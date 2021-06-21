/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileSorting;

/**
 *
 * @author amnwaqar
 */

import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class FileSorterGUI extends JPanel implements PropertyChangeListener {
    public final int PANEL_WIDTH = 700; public final int PANEL_HEIGHT = 700;
    private DrawingPanel drawPanel;
    public JButton addTask, executeTask;
    public JLabel text;
    private JProgressBar[] progressBar;
    private JLabel[] labels;
    public int numTasks, tasksExecuted;
    private JTextArea taskOutput;
    FileSorter task;
    File[] files;
    Queue<FileSorter> tasks;

    public FileSorterGUI() {
        super(new BorderLayout()); 
        tasks = new LinkedList<>();
        labels = new JLabel[4];
        files = new File[4];
        files[0] = new File("./resources/groceries.txt");
        files[1] = new File("./resources/animals.txt");
        files[2] = new File("./resources/flowers.txt");
        files[3] = new File("./resources/countries.txt");
        numTasks = 0;
        tasksExecuted = 0;
        
        JPanel progressBars= new JPanel();
        
        progressBar = new JProgressBar[4];
        
        drawPanel = new DrawingPanel();     //create DrawingPanel and add to center
        add(drawPanel, BorderLayout.CENTER);
        
        for (int k = 0; k < 4; k++)
        {
            progressBar[k] = new JProgressBar(0,100);
            progressBar[k].setSize(100, 100);
            labels[k] = new JLabel("task " + (k+1) + " progess", JLabel.CENTER);
            drawPanel.add(progressBar[k]);
            drawPanel.add(labels[k]);
        }
        
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);
        taskOutput.append(String.format("Notes:\n    # only execute a task after the previous task is finished.\n    # all output files will be deleted upon exiting window.\n"));
        
        drawPanel.add(taskOutput);
        
        text = new JLabel("add task to begin!", JLabel.CENTER);
        add(text, BorderLayout.NORTH);
        
        JPanel buttons = new JPanel();
        
        addTask = new JButton("Add Task");
        addTask.addActionListener(action -> addTask());
        buttons.add(addTask);
        
        executeTask = new JButton("Execute Task");
        executeTask.addActionListener(action -> executeTask());
        buttons.add(executeTask);
        
        add(buttons, BorderLayout.SOUTH);
        
    }
    
    private class DrawingPanel extends JPanel
    {   public DrawingPanel()
        {   
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(Color.WHITE);
        }
    
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
        }
    }
    
    public static void main(String[] args) {
        FileSorterGUI myPanel = new FileSorterGUI();
        JFrame frame = new JFrame("File Sorter"); //create frame to hold our JPanel subclass	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(myPanel);  //add instance of MyGUI to the frame
        frame.pack(); //resize frame to fit our Jpanel
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(new Point((d.width / 2) - (frame.getWidth() / 2), (d.height / 2) - (frame.getHeight() / 2)));
	//show the frame	
        frame.setVisible(true);
    }
    
    public void addTask()
    {
        if (numTasks < files.length)
        {
            File output = new File("./resources/output" + numTasks +".txt");
            
            try
            {
                output.createNewFile();
                tasks.add(new FileSorter(5, files[numTasks], output));
                numTasks++;
                text.setText("task " + numTasks + " added to queue!");
                
            } catch (IOException e)
            {
                System.out.println("ERROR. Could not create file!");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "No remaining tasks", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void executeTask()
    {
        if (!tasks.isEmpty())
        {
            task = tasks.poll();
            taskOutput.append(String.format("\nExecuting task " + (tasksExecuted+1) +":\n\n"));
            task.addPropertyChangeListener(this);
            task.execute();
            tasksExecuted++;
            text.setText("executing task " + tasksExecuted + "!");
        }
        else
        {
            JOptionPane.showMessageDialog(this, "No tasks in queue", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
    if ("progress" == evt.getPropertyName()) {
      int progress = (Integer) evt.getNewValue();
      progressBar[tasksExecuted - 1].setValue(progress);
      taskOutput.append(String.format("     Completed %d%% of task.\n", task.getProgress()));
      drawPanel.repaint();
    }
  }
}
