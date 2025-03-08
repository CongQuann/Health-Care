module com.sixthgroup.healthmanagementtraining {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.sixthgroup.healthmanagementtraining to javafx.fxml;
    exports com.sixthgroup.healthmanagementtraining;
}
