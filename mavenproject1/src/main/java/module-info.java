module com.sixthgroup.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.sixthgroup.mavenproject1 to javafx.fxml;
    exports com.sixthgroup.mavenproject1;
}
