package acadflow.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;


public class FileStorageHandler {

    private static final String IMAGE_UPLOAD_DIR = "/profile_pics/";
    private static final String FILE_UPLOAD_DIR = "/course_materials/";
    private static final long MAX_IMAGE_FILE_SIZE = 2 * 1024 * 1024; // 2MB in bytes
    private static final long MAX_MATERIAL_FILE_SIZE = 5 * 1024 * 1024; // 2MB in bytes
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};
    private static final String[] ALLOWED_MATERIAL_EXTENSIONS = {"pdf", "pptx", "docx"};

    public static String addMaterial(File material) throws IOException {
        if (isInValidFileType(material.getName(), ALLOWED_MATERIAL_EXTENSIONS)) {
            showError("Invalid File Type",
                    "Only pdf, pptx, and docx files are allowed.\n" +
                            "Please select a valid file type.");
            return null;
        }

        if (isInValidFileSize(material.length(), MAX_MATERIAL_FILE_SIZE)) {
            double sizeInMB = (double) material.length() / 1024 / 1024;
            String formattedSizeInMB = String.format("%.2f",sizeInMB);
            showError("File Too Large",
                    "The selected image is " + formattedSizeInMB + " MB.\n" +
                            "Maximum allowed size is 5MB (5120 KB).\n" +
                            "Please choose a smaller File.");
            return null;
        }

        return saveToDirectory(material,FILE_UPLOAD_DIR);
    }

    public static String saveProfileImage(File imageFile) throws IOException {
        if (isInValidFileType(imageFile.getName(), ALLOWED_EXTENSIONS)) {
            showError("Invalid File Type",
                    "Only JPG, JPEG, and PNG files are allowed.\n" +
                            "Please select a valid image file.");
            return null;
        }

        if (isInValidFileSize(imageFile.length(), MAX_IMAGE_FILE_SIZE)) {
            double sizeInMB = (double) imageFile.length() / 1024 / 1024;
            String formattedSizeInMB = String.format("%.2f",sizeInMB);
            showError("File Too Large",
                    "The selected image is " + formattedSizeInMB + " MB.\n" +
                            "Maximum allowed size is 2MB (2048 KB).\n" +
                            "Please choose a smaller image.");
            return null;
        }

        return saveToDirectory(imageFile,IMAGE_UPLOAD_DIR);
    }

    private static boolean isInValidFileType(String fileName, String[] extensions) {
        String ext = getFileExtension(fileName).toLowerCase();
        for (String allowed : extensions) {
            if (allowed.equals(ext)) return false;
        }
        return true;
    }

    private static boolean isInValidFileSize(long fileSizeBytes, long maxFileSizeBytes) {
        return fileSizeBytes > maxFileSizeBytes;
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

    private static String saveToDirectory(File imageFile,String uploadDirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);

        //create upload directory if not exists
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        //Generate unique name
        String extension = getFileExtension(imageFile.getName());
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

        Path destination = uploadPath.resolve(uniqueFileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uploadDirectory + uniqueFileName;
    }
}
