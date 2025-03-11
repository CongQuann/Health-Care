package com.sixthgroup.healthmanagementtraining;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

public class SignUpController {

    @FXML
    private ComboBox<String> TypeExercise;
    
    public void initialize() {
        TypeExercise.setItems(FXCollections.observableArrayList(
            "Ít vận động", "Vận động nhẹ", "Vận động vừa phải", "vận động nhiều", "Vận động thường xuyên"
        ));

        // Đặt giá trị mặc định
        TypeExercise.setValue("Vận động nhẹ");
    }
}