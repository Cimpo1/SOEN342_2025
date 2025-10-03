import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Quick {

    public static void main(String[] args) {
        // Create the main window (JFrame)
        JFrame frame = new JFrame("My Quick GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        frame.setSize(1000, 800); // Set window size
        frame.setLayout(null); // Use absolute positioning for simplicity

        // Create a label
        JLabel label = new JLabel("Hello, world!");
        label.setBounds(50, 30, 200, 30); // Set position and size
        frame.add(label); // Add label to the frame

        // Create a button
        JButton button = new JButton("Click Me!");
        button.setBounds(50, 80, 100, 30); // Set position and size
        frame.add(button); // Add button to the frame

        // Add an action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("Button clicked!"); // Change label text on click
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}