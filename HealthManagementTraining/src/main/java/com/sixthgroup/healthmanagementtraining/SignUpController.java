package com.sixthgroup.healthmanagementtraining;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

public class SignUpController {

    @FXML
    private ComboBox<String> TypeExercise;
    public void initialize() {
        System.out.println("ComboBox TypeExercise đã được nhận diện.");
        TypeExercise.setItems(FXCollections.observableArrayList(
            "Ít vận động", "Vận động nhẹ", "Vận động vừa phải", "Vận động nhiều", "Vận động thường xuyên"
        ));
        TypeExercise.setValue("Ít vận động");
}
}