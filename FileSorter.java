import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSorter {
    public static void main(String[] args) {
        String sourceDirectory = "";
        String destinationDirectory = "";

        File sourceDir = new File(sourceDirectory);
        File destDir = new File(destinationDirectory);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.err.println("Source directory does not exist.");
            return;
        }

        if (!destDir.exists() || !destDir.isDirectory()) {
            System.err.println("Destination directory does not exist.");
            return;
        }

        File[] files = sourceDir.listFiles();

        if (files != null) {
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
                        System.out.println("Moved " + file.getName() + " to " + destPath.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // No extension
    }
}
