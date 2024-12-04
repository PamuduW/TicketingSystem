import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Console {
    private final JFrame frame; // Frame for displaying the simulation
    private final JTextArea outputArea; // Text area for displaying output
    private String log = ""; // Log to store simulation messages
    private String saveFileName; // Name of the file to save the log
    public static boolean saveFile; // Flag to indicate if the log should be saved

    // Constructor to initialize the console
    public Console() {
        frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700, 800);

        outputArea = new JTextArea();
        outputArea.setBackground(Color.LIGHT_GRAY);
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    // Method to set the frame visibility
    public void setFrameVisibility() {
        frame.setVisible(true);
    }

    // Method to close the frame
    public void closeFrame() {
        frame.dispose();
    }

    // Method to append output to the text area and log
    public void appendOutput(String message) {
        SwingUtilities.invokeLater(() -> outputArea.append("   " + message + "\n"));
        log += message + "\n";
    }

    // Method to get permission from the user to save the log file
    public boolean getSaveFilePermission() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Do you want to save the simulation log to a json file? (y/n) : ");
            String choice = scanner.nextLine();

            if ("y".equalsIgnoreCase(choice)) {
                saveFile = true;
                break;
            } else if ("n".equalsIgnoreCase(choice)) {
                saveFile = false;
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
        return saveFile;
    }

    // Method to get the file name from the user to save the log
    public void getSaveFileName() {
        while (true) {
            System.out.print("Enter save file name : ");
            saveFileName = "Logs\\" + new Scanner(System.in).nextLine() + ".json";
            File saveFileCheck = new File(saveFileName);
            if (saveFileCheck.exists()) {
                System.out.println("A file with that file name already exists. Choose something else");
                continue;
            }
            break;
        }
    }

    // Method to save the log to a json file
    public void saveJsonFile() {
        try {
            try (FileWriter file = new FileWriter(saveFileName)) {
                file.write(log); // Write the log to the file
            }
        } catch (IOException e) {
            System.out.println("Error while saving json file");
        }
    }
}