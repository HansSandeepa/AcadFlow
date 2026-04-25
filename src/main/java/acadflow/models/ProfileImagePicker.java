package acadflow.models;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProfileImagePicker {

    public static File pickImage(Stage ownerStage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files (JPG, JPEG, PNG)", "*.jpg", "*.jpeg", "*.png"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        return fileChooser.showOpenDialog(ownerStage);
    }
}
