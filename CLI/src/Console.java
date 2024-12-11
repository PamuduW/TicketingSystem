import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Console {
    private final JFrame frame;
    private final JTextArea outputArea;
    private String log = "";
    private String saveFileName;
    public static boolean saveFile;

    public Console() {
        frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(700, 800);

        outputArea = new JTextArea();
        outputArea.setBackground(Color.LIGHT_GRAY);
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public void setFrameVisibility() {
        frame.setVisible(true);
    }

    public void closeFrame() {
        frame.dispose();
    }

    public void appendOutput(String message) {
        SwingUtilities.invokeLater(() -> outputArea.append("   " + message + "\n"));
        log += message + "\n";
    }

    public boolean getSaveFilePermission() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Do you want to save the simulation log as a JSON file? (y/n): ");
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

    public void getSaveFileName() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the save file name: ");
            saveFileName = "Logs\\" + scanner.nextLine() + ".json";
            if (new File(saveFileName).exists()) {
                System.out.println("A file with that file name already exists. Choose something else");
            } else {
                break;
            }
        }
    }

    public void saveJsonFile() {
        try (FileWriter file = new FileWriter(saveFileName)) {
            file.write(log);
        } catch (IOException e) {
            System.out.println("Error while saving json file");
        }
    }
}