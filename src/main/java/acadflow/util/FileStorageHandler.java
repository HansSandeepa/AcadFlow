package acadflow.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;


public class FileStorageHandler {

    private static final String UPLOAD_DIR = "/profile_pics/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB in bytes
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};

    public static String saveProfileImage(File imageFile) throws IOException {
        if (!isValidFileType(imageFile.getName())) {
            showError("Invalid File Type",
                    "Only JPG, JPEG, and PNG files are allowed.\n" +
                            "Please select a valid image file.");
            return null;
        }

        if (!isValidFileSize(imageFile.length())) {
            double sizeInMB = (double) imageFile.length() / 1024 / 1024;
            String formattedSizeInMB = String.format("%.2f",sizeInMB);
            showError("File Too Large",
                    "The selected image is " + formattedSizeInMB + " MB.\n" +
                            "Maximum allowed size is 2MB (2048 KB).\n" +
                            "Please choose a smaller image.");
            return null;
        }

        return saveToDirectory(imageFile);
    }

    private static boolean isValidFileType(String fileName) {
        String ext = getFileExtension(fileName).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(ext)) return true;
        }
        return false;
    }

    private static boolean isValidFileSize(long fileSizeBytes) {
        return fileSizeBytes <= MAX_FILE_SIZE;
    }

    private static String getFileExtension(String fileName) {
        return fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1)
                : ""; // default fallback
    }


    private static void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static String saveToDirectory(File imageFile) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        //create upload directory if not exists
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        //Generate unique name
        String extension = getFileExtension(imageFile.getName());
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

        Path destination = uploadPath.resolve(uniqueFileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return UPLOAD_DIR + uniqueFileName;
    }
}
