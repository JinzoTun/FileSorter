import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSorterGUI {
    private JTextArea debugTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        FileSorterGUI fileSorterGUI = new FileSorterGUI();
        fileSorterGUI.buildGUI();
    }

    private void buildGUI() {
        JFrame frame = new JFrame("File Sorter GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon("icon.png");
        frame.setIconImage(icon.getImage());

        frame.setSize(600, 400);
        frame.setMinimumSize(new Dimension(400, 150));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel sourceLabel = new JLabel("Source Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(sourceLabel, gbc);

        JTextField sourcePathField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(sourcePathField, gbc);

        JButton sourceBrowseButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(sourceBrowseButton, gbc);

        JLabel destinationLabel = new JLabel("Destination Path:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(destinationLabel, gbc);

        JTextField destinationPathField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(destinationPathField, gbc);

        JButton destinationBrowseButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(destinationBrowseButton, gbc);

        JButton sortButton = new JButton("Sort Files");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(sortButton, gbc);

        sourceBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPath(sourcePathField);
            }
        });

        destinationBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPath(destinationPathField);
            }
        });

        debugTextArea = new JTextArea();
        debugTextArea.setEditable(false);
        debugTextArea.setLineWrap(true);
        JScrollPane debugScrollPane = new JScrollPane(debugTextArea);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        debugScrollPane
                .setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(debugScrollPane, gbc);

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceDirectory = sourcePathField.getText();
                String destinationDirectory = destinationPathField.getText();
                sortFiles(sourceDirectory, destinationDirectory);
            }
        });

        frame.setVisible(true);
    }

    private void selectPath(JTextField pathField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int option = fileChooser.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            pathField.setText(selectedFilePath);
        }
    }

    private void sortFiles(String sourceDirectory, String destinationDirectory) {
        File sourceDir = new File(sourceDirectory);
        File destDir = new File(destinationDirectory);
        int successfulMoves = 0;

        if (sourceDirectory.isEmpty() || destinationDirectory.isEmpty()) {
            // Check if either source or destination path is empty
            JOptionPane.showMessageDialog(null, "Source and destination paths cannot be empty.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            debugTextArea.append("Source directory does not exist.\n");
            return;
        }

        if (!destDir.exists() || !destDir.isDirectory()) {
            debugTextArea.append("Destination directory does not exist.\n");
            return;
        }

        File[] files = sourceDir.listFiles();

        if (files != null) {
            boolean filesMoved = false;

            for (File file : files) {
                if (file.isFile()) {
                    String extension = getFileExtension(file.getName());
                    File destSubDir = new File(destDir, extension);

                    if (!destSubDir.exists()) {
                        destSubDir.mkdir();
                    }

                    Path sourcePath = file.toPath();
                    Path destPath = Paths.get(destSubDir.toString(), file.getName());

                    try {
                        Files.move(sourcePath, destPath);
                        debugTextArea.append("Moved " + file.getName() + " to " + destPath.toString() + "\n");
                        successfulMoves++;
                        filesMoved = true;
                    } catch (IOException ex) {
                        debugTextArea.append("Error moving " + file.getName() + ": " + ex.getMessage() + "\n");
                        ex.printStackTrace();
                    }
                }
            }

            if (!filesMoved) {
                // No files to move
                JOptionPane.showMessageDialog(null, "No files to move :)", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No files to move :)", "Information", JOptionPane.INFORMATION_MESSAGE);
        }

        if (successfulMoves > 0) {
            debugTextArea.append("Number of files moved successfully: " + successfulMoves + "\n");
            JOptionPane.showMessageDialog(null, "Sorting complete", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // No extension
    }
}
