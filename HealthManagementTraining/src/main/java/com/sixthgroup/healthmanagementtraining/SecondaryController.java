package com.sixthgroup.healthmanagementtraining;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    private void switchToSignUp() throws IOException {
        App.setRoot("SignUp");
    } 
}