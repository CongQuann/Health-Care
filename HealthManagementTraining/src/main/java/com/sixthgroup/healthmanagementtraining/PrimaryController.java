package com.sixthgroup.healthmanagementtraining;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    private void switchToSignUp() throws IOException {
        App.setRoot("SignUp");
    }
}
